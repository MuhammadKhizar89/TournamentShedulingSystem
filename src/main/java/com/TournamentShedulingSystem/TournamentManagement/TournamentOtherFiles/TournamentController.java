package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles.CommentsService;
import com.TournamentShedulingSystem.UserManagement.ActorOtherFiles.ActorService;
import org.springframework.jdbc.core.JdbcTemplate;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles.MatchService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles.PlayerService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCard;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCardOtherFiles.ScoreCardService;
import com.TournamentShedulingSystem.TournamentManagement.Schedule;
import com.TournamentShedulingSystem.TournamentManagement.ScheduleOtherFiles.ScheduleService;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
@Controller
public class TournamentController {
    private TournamentService tservice;
    private PlayerService playerService;
    private MatchService matchService;
    private ScheduleService scheduleService;
    private ScoreCardService scoreCardService;
    private TeamNameRepository teamNameRepository;
    private CommentsService commentsService;
    private ActorService actorService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public TournamentController(TournamentService tservice, PlayerService playerService, MatchService matchService, ScheduleService scheduleService, ScoreCardService scoreCardService, TeamNameRepository teamNameRepository, CommentsService commentsService, ActorService actorService) {
        this.tservice = tservice;
        this.playerService = playerService;
        this.matchService = matchService;
        this.scheduleService = scheduleService;
        this.scoreCardService = scoreCardService;
        this.teamNameRepository = teamNameRepository;
        this.commentsService = commentsService;
        this.actorService = actorService;
    }
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("username") != null) {
            model.addAttribute("loggedIn", true);
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mytournaments")
    public String MyTournament(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("userId") == null) {
            return "redirect:/signup";
        } else {
            System.out.println(request.getSession().getAttribute("userId"));
            Long userId = (Long) request.getSession().getAttribute("userId"); // Get userId from session
            List<Tournament> allTournaments = tservice.findAllTournaments();
            List<Tournament> userTournaments = filterTournamentsByAdminId(allTournaments, userId);
            model.addAttribute("tournaments", userTournaments);
            return "EditTournament";
        }
    }

    private List<Tournament> filterTournamentsByAdminId(List<Tournament> tournaments, Long userId) {
        List<Tournament> filteredTournaments = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            Long adminId = tournament.getUserId();
            if (adminId != null && adminId.equals(userId)) {
                filteredTournaments.add(tournament);
            }
        }
        return filteredTournaments;
    }

    @GetMapping("/tournament")
    public String listTournament(Model model, @RequestParam(required = false) String searchCriteria, HttpServletRequest request) {
        if (searchCriteria != null) {
            String queryStr = switch (searchCriteria) {
                case "All" -> "SELECT * FROM tournament";
                case "Date" -> "SELECT * FROM tournament WHERE tournament_start_date = ?";
                case "Teams" -> "SELECT * FROM tournament WHERE no_of_teams = ?";
                case "Keyword" -> "SELECT DISTINCT tournament.id " +
                        "FROM tournament " +
                        "JOIN umpire ON tournament.id = umpire.tournament_id " +
                        "JOIN venue ON tournament.id = venue.tournament_id " +
                        "JOIN team_name ON tournament.id = team_name.tournament_id " +
                        "JOIN players ON team_name.id = players.team_id " +
                        "WHERE tournament.name_of_tournament LIKE ? OR " +
                        "umpire.umpire_name LIKE ? OR " +
                        "venue.venue_name LIKE ? OR " +
                        "players.name LIKE ?";
                default -> "SELECT * FROM tournament";
            };

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(queryStr)) {

                switch (searchCriteria) {
                    case "Date":
                        String dateString = request.getParameter("searchParam"); // Retrieve date string from request
                        Date date = parseDate(dateString); // Parse the date string into a Date object

                        // Convert the date into the required format
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000000");
                        String formattedDate = outputFormat.format(date);

                        preparedStatement.setString(1, formattedDate);
                        break;

                    case "Teams":
                        String teamsString = request.getParameter("searchParam"); // Retrieve number of teams string from request
                        int noOfTeams = Integer.parseInt(teamsString); // Convert string to integer
                        preparedStatement.setInt(1, noOfTeams);
                        break;

                    case "Keyword":
                        String keyword = "%" + request.getParameter("searchParam") + "%";
                        for (int i = 1; i <= 4; i++) {
                            preparedStatement.setString(i, keyword);
                        }
                        break;
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                List<Tournament> tournaments = new ArrayList<>();
                while (resultSet.next()) {
                    Optional<Tournament> tournament1 = Optional.of(new Tournament());
                    int i = (int) resultSet.getLong("id");
                    tournament1 = tservice.getTournamentInfo(i);
                    Tournament tournament2 = tournament1.get();
                    tournaments.add(tournament2);
                }
                if (tournaments != null) {
                    model.addAttribute("tournament", tournaments);
                } else {
                    model.addAttribute("error", "No Such Tournament Found"); // Add error message to model
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return "Tournament-list";
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing exception appropriately
            return null;
        }
    }
    @GetMapping("/tournamentnew")
    public String createTournament(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("userId") == null) {
            return "redirect:/signup";
        } else {
            model.addAttribute("tournamentForm", new TournamentForm());
            return "CreatingTournament/creating-Tournament";
        }
    }


    @PostMapping("/tournamentnew")
    public String processTournamentForm(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Parse start and end dates
            LocalDate startDate = LocalDate.parse(tournamentForm.getTournamentStartDate());
            LocalDate endDate = LocalDate.parse(tournamentForm.getTournamentEndDate());

            // Check if end date is before start date
            if (endDate.isBefore(startDate)) {
                redirectAttributes.addFlashAttribute("error", "End date cannot be before start date.");
                return "redirect:/tournamentnew"; // Return to the form page with an error message
            }
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format.");
            return "redirect:/tournamentnew"; // Return to the form page with an error message for invalid date format
        }

        session.setAttribute("tournamentForm", tournamentForm);
        return "redirect:/tournament2"; // Redirect to the next step
    }

    @GetMapping("/tournament2")
    public String showTournament2Form(HttpSession session, Model model) {
        TournamentForm tournamentForm = (TournamentForm) session.getAttribute("tournamentForm");
        if (tournamentForm == null) {
            // Redirect to the first form if no data is available
            return "redirect:/tournamentnew";
        }
        model.addAttribute("tournamentForm", tournamentForm);
        return "CreatingTournament/creating-Tournament4";
    }

    @PostMapping("/tournament2")
    public String OrganizeTournament(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, HttpSession session, HttpServletRequest request, Model model) {
        Long userId = (Long) request.getSession().getAttribute("userId"); // Get userId from session
        // Parse tournament start date
        Date tournamentStartDate = null;
        try {
            tournamentStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(tournamentForm.getTournamentStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return "error-page"; // Handle parsing exception
        }
        // Parse tournament end date
        Date tournamentEndDate = null;
        try {
            tournamentEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(tournamentForm.getTournamentEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return "error-page"; // Handle parsing exception
        }
        // Create a new Tournament object
        Tournament tournament = new Tournament();
        // Set tournament attributes
        tournament.setUserId(userId);
        tournament.setTournamentDuration(tournamentForm.getTournamentDuration());
        tournament.setNoOfTeams(tournamentForm.getNoOfTeams());
        tournament.setTournamentStartDate(tournamentStartDate);
        tournament.setTournamentEndDate(tournamentEndDate);
        tournament.setNameOfTournament(tournamentForm.getNameOfTournament());
        tournament.setTypeOfTournament(tournamentForm.getTypeOfTournament());
        // Create lists to hold venues, umpires, and team names
        List<Tournament.Venue> venues = new ArrayList<>();
        List<Tournament.Umpire> umpires = new ArrayList<>();
        List<Tournament.TeamName> teamNames = new ArrayList<>();
        // Populate venues
        for (String venueName : tournamentForm.getVenueList()) {
            Tournament.Venue venue = new Tournament.Venue();
            venue.setVenueName(venueName.trim());
            venue.setTournament(tournament);
            venues.add(venue);
        }
        // Populate umpires
        for (String umpireName : tournamentForm.getUmpireList()) {
            Tournament.Umpire umpire = new Tournament.Umpire();
            umpire.setUmpireName(umpireName.trim());
            umpire.setTournament(tournament);
            umpires.add(umpire);
        }
        // Populate team names
        for (String teamName : tournamentForm.getTeamNames()) {
            Tournament.TeamName teamNameObject = new Tournament.TeamName();
            teamNameObject.setTeamName(teamName.trim());
            teamNameObject.setTournament(tournament);
            teamNameObject.setPoints(0);
            teamNameObject.setNetRunRate(0.0);
            teamNames.add(teamNameObject);
        }
        // Set the lists of venues, umpires, and team names to the tournament
        tournament.setVenues(venues);
        tournament.setUmpires(umpires);
        tournament.setTeamNames(teamNames);
        // Save the tournament along with its associated entities
        tservice.OrganizeTournament(tournament);
        session.setAttribute("tournament", tournament);
        return "redirect:/tournament3";
    }

    @GetMapping("/tournament3")
    public String showTournament3Form(HttpSession session, Model model) {
        // Retrieve the Tournament object from the session
        Tournament tournament = (Tournament) session.getAttribute("tournament");
        // Check if the tournament object is not null
        if (tournament != null) {
            System.out.println("No of teams: " + tournament.getNoOfTeams());
            System.out.println("Tournament Start Date: " + tournament.getTournamentStartDate());
            System.out.println("Tournament Name: " + tournament.getTeamNames());
        }
        else {
            return "error1";
        }
        model.addAttribute("tournament", tournament);
        return "CreatingTournament/creating-Tournament5";
    }

    @PostMapping("/tournament3")
    public String submitFormDetails(HttpServletRequest request, HttpSession session, Model model) {
        Tournament tournament = (Tournament) session.getAttribute("tournament");
        List<Integer> teamNameIds = tournament.getTeamNameIds();
        Enumeration<String> parameterNames = request.getParameterNames();
        int count = 0;
        int x = 0;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                String playerName = value;
                Player player = new Player();
                if (count % 11 == 0 && count > 0) {
                    x++;
                }
                player.setTeamId(teamNameIds.get(x));
                player.setName(playerName);
                player.setRuns(0);
                player.setTotalRuns(0);
                player.setWickets(0);
                player.setTotalWickets(0);
                playerService.addPlayerInfo(player);
                count++;
            }
        }
        model.addAttribute("tournament", tournament);
        return "ShedulingTournament/ShedulingTournament1";
    }

//    @PostMapping("/confirmSchedule")
//    public String confirmSchedule(@RequestParam List<Match> matches, Model model, @SessionAttribute("tournament") Tournament tournament) {
//        if (tournament == null) {
//            model.addAttribute("error", "Tournament details not found in session");
//            return "redirect:/schedule2"; // You need to create an error page
//        }
//        LocalDateTime currentDate = LocalDateTime.now();
//        LocalDateTime tournamentStartDate = tournament.getTournamentStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        LocalDateTime tournamentEndDate = tournament.getTournamentEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        // Check if current date is within tournament start and end dates
//        if (currentDate.isBefore(tournamentStartDate) || currentDate.isAfter(tournamentEndDate)) {
//            model.addAttribute("error", "Current date is not within tournament start and end dates");
//            return "redirect:/schedule2"; // You need to create an error page
//        }
//        // Check each match date against tournament start and end dates
//        for (Match match : matches) {
//            LocalDateTime matchDateTime = match.getMatchDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//            if (matchDateTime.isBefore(tournamentStartDate) || matchDateTime.isAfter(tournamentEndDate)) {
//                model.addAttribute("error", "Match date is not within tournament start and end dates");
//                return "redirect:/schedule2"; // You need to create an error page
//            }
//        }
//
//        return "redirect:/"; // You need to create a confirmation page
//    }
//@PostMapping("/confirmSchedule")
//public String confirmSchedule(HttpServletRequest request, Model model) {
//    List<Match> matches = new ArrayList<>();
//
//    // Retrieve the number of matches from the request parameter
//    int numberOfMatches = Integer.parseInt(request.getParameter("numberOfMatches"));
//    // Iterate through each match to retrieve umpire, venue, and match time
//    // Inside your controller method
//    for (int i = 0; i < numberOfMatches; i++) {
//        // Retrieve umpire, venue, and match time from request parameters
//        String umpire = request.getParameter("umpire" + i);
//        String venue = request.getParameter("venue" + i);
//        String matchTimeStr = request.getParameter("matchTime" + i);
//
//        // Convert match time String to Date
//        Date matchTime;
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//            matchTime = dateFormat.parse(matchTimeStr);
//        } catch (ParseException e) {
//            e.printStackTrace(); // Handle parsing exception
//            // Set matchTime to null or some default value if parsing fails
//            matchTime = null;
//        }
//        // Create a new Match object and set umpire, venue, and match time
//        Match match = new Match();
//        match.setUmpire(umpire);
//        match.setVenue(venue);
//        match.setMatchDate(matchTime);
//        // Add the match to the list of matches
//        matches.add(match);
//    }
//    // Print all matches along with their corresponding umpire, venue, and match time
//    for (Match match : matches) {
//        System.out.println("Match: " + match.getTeam1() + " vs " + match.getTeam2());
//        System.out.println("Umpire: " + match.getUmpire());
//        System.out.println("Venue: " + match.getVenue());
//        System.out.println("Match Time: " + match.getMatchDate());
//        System.out.println();
//    }
//
//    model.addAttribute("matches", matches);
//
//    return "confirmationPage";
//}

//    @PostMapping("/confirmSchedule")
//    public String confirmSchedule(HttpServletRequest request,HttpSession session,
//                                  Model model) {
//        Tournament tournament = (Tournament) session.getAttribute("tournament");
//        List<Match> allMatches  = (List<Match>) session.getAttribute("allMatches");
//        Enumeration<String> parameterNames = request.getParameterNames();
//        while (parameterNames.hasMoreElements()) {
//            String paramName = parameterNames.nextElement();
//            String[] paramValues = request.getParameterValues(paramName);
//            for (String value : paramValues) {
//                System.out.println("Parameter: " + paramName + ", Value: " + value);
//            }
//        }
//        return "confirmation-page"; // Return the name of the confirmation page
//    }
@PostMapping("/confirmSchedule")
@Transactional
public String confirmSchedule(HttpServletRequest request, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    Tournament tournament = (Tournament) session.getAttribute("tournament");
    List<String> umpires = new ArrayList<>();
    List<String> venues = new ArrayList<>();
    List<Date> date1 = new ArrayList<>();
    List<Match> allMatches = (List<Match>) session.getAttribute("allMatches");
    if (tournament == null || allMatches == null) {
        return "error1";
    }
    int x=(allMatches.size()*3)+2;
    System.out.println("------->"+x);
    int y=1;
    int index=0;
    int u=allMatches.size()+2;
    int v=allMatches.size()*2+2;
    Enumeration<String> parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
        String paramName = parameterNames.nextElement();
        String[] paramValues = request.getParameterValues(paramName);
        for (String value : paramValues) {
            System.out.println("Parameter: " + paramName + ", Value: " + value);
            y++;
            if (y>x) {
                try {
                    System.out.println("Parameter1111111111");
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(value);
                    if (!isValidTournamentDate(date, tournament)) {
                        redirectAttributes.addFlashAttribute("error", "Date should be within the tournament start and end dates.");
                        return "redirect:/schedule2"; // Return error page
                    }
//                    allMatches.get(index).setMatchDate(date);
                    date1.add(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(y>u&&y<=v)
            {
                venues.add(value);
            }
            else if(y>v&&y<=x)
            {
                umpires.add(value);
            }
        }
    }
    int size = date1.size();
    if (size >= 3) {
        Date lastDate = date1.get(size - 1);
        Date secondLastDate = date1.get(size - 2);
        Date thirdLastDate = date1.get(size - 3);
        if (lastDate.after(secondLastDate) && (secondLastDate.after(thirdLastDate)) || secondLastDate.equals(thirdLastDate)) {
            boolean lastThreeAfterAllMatches = true;
            for (int i = 0; i < size - 3; i++) {
                if (!(lastDate.after(date1.get(i)) || secondLastDate.after(date1.get(i)) || thirdLastDate.after(date1.get(i)))) {
                    lastThreeAfterAllMatches = false;
                    System.out.println(lastDate);
                    System.out.println(i);
                    System.out.println(date1.get(i));
                    System.out.println(secondLastDate);
                    break;
                }
            }
            if (lastThreeAfterAllMatches) {
                if (!lastDate.before(secondLastDate)) {
                    if (!secondLastDate.before(thirdLastDate) ) {
                        // Assign umpires, venues, and dates to all matches
                        int o = 0;
                        for (Match m : allMatches) {
                            m.setUmpire(umpires.get(o));
                            m.setVenue(venues.get(o));
                            m.setMatchDate(date1.get(o));
                            o++;
                        }
                        Comparator<Match> matchComparator = new Comparator<Match>() {
                            @Override
                            public int compare(Match match1, Match match2) {
                                // Compare the match dates
                                int dateComparison = match1.getMatchDate().compareTo(match2.getMatchDate());
                                if (dateComparison != 0) {
                                    // If dates are different, return the result of date comparison
                                    return dateComparison;
                                } else {
                                    // If dates are the same, compare the times
                                    return match1.getMatchDate().compareTo(match2.getMatchDate());
                                }
                            }
                        };
                        allMatches.sort(matchComparator);
                        matchService.saveAllMatches(allMatches);
                        System.out.println("All" + umpires);
                        System.out.println("All" + venues);
                        System.out.println("All" + date1);
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Second last match is before the third last match");
                        return "redirect:/schedule2"; // Return error page
                    }
                } else {
                    redirectAttributes.addFlashAttribute("error", "Last match is before the second last match");
                    return "redirect:/schedule2"; // Return error page
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Last three matches are not after all the other matches");
                return "redirect:/schedule2"; // Return error page
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Last three matches are not in sequence");
            return "redirect:/schedule2"; // Return error page
        }
    } else {
        redirectAttributes.addFlashAttribute("error", "There are not enough matches to proceed");
        return "redirect:/schedule2"; // Return error page
    }

    String createTableQuery = "CREATE TABLE IF NOT EXISTS PlayerStats (id INT AUTO_INCREMENT PRIMARY KEY, matchId INT, playerId INT, runs INT, wickets INT)";
    jdbcTemplate.execute(createTableQuery);
    int currentTournamentId = tournament.getId();
    List<Match> scheduledMatches = matchService.getAllMatchesByTournamentId(currentTournamentId);
    for (Match match : scheduledMatches) {
        List<Player> team1Players = playerService.getPlayersInfo(match.getTeam1id());
        List<Player> team2Players = playerService.getPlayersInfo(match.getTeam2id());
        for (Player player : team1Players) {
            String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, match.getId(), player.getId(), 0, 0);
        }
        // Insert data for team 2 players
        for (Player player : team2Players) {
            String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, match.getId(), player.getId(), 0, 0); // Set runs and wickets to 0 for team 2 players
        }
    }
    int updatedRows = entityManager.createNativeQuery(
                    "INSERT INTO NoofMatches (tournamentid, noofmatches) " +
                            "VALUES (?, ?) ")
            .setParameter(1, tournament.getId())
            .setParameter(2, allMatches.size())
            .executeUpdate();
    return "redirect:/";
}

    private boolean isValidTournamentDate(Date date, Tournament tournament) {
        // Validate the date against tournament start and end dates
        return !date.before(tournament.getTournamentStartDate()) && !date.after(tournament.getTournamentEndDate());
    }

    @GetMapping("/schedule2")
    public String ManuallyTournamentSchedule(HttpServletRequest request, HttpSession session, Model model) {
        Tournament tournament = (Tournament) session.getAttribute("tournament");
        List<String> umpires = tournament.getUmpires();
        List<String> venues = tournament.getVenues();
        List<Match> allMatches = generateAllMatches(tournament);
        session.setAttribute("allMatches", allMatches);
        model.addAttribute("tournament", tournament);
        model.addAttribute("allMatches", allMatches);
        model.addAttribute("umpires", umpires);
        model.addAttribute("venues", venues);
        return "ShedulingTournament/ShedulingTournament2";
    }

    private List<Match> generateAllMatches(Tournament tournament) {
        List<Integer> teamNameIds = tournament.getTeamNameIds();
        List<String> teamNames = tournament.getTeamNames();
        List<String> umpires = tournament.getUmpires();
        List<String> venues = tournament.getVenues();
        int tournamentId = tournament.getId();
        List<Match> matches = new ArrayList<>();
        // Generate matches for each combination of teams
        for (int i = 0; i < teamNames.size(); i++) {
            for (int j = i + 1; j < teamNames.size(); j++) {
                Match match = new Match();
                match.setTournamentid(tournamentId);
                match.setTeam1(teamNames.get(i));
                match.setTeam2(teamNames.get(j));
                match.setTeam1id(teamNameIds.get(i));
                match.setTeam2id(teamNameIds.get(j));
                match.setUmpire(getRandomItemFromList(umpires));
                match.setVenue(getRandomItemFromList(venues));
                match.setComplete(false);
                matches.add(match);
            }
        }
        addAdditionalMatches(matches, 3, tournamentId, umpires, venues);
        return matches;
    }

    @PostMapping("/schedule1")
    @Transactional
    public String automateTornamentShedule(HttpServletRequest request, HttpSession session, Model model) {
        Tournament tournament = (Tournament) session.getAttribute("tournament");
        model.addAttribute("showLoadingModal", true);
        List<Integer> teamNameIds = tournament.getTeamNameIds();
        List<String> teamNames = tournament.getTeamNames();
        List<String> umpires = tournament.getUmpires();
        List<String> venues = tournament.getVenues();
        Date startDate = tournament.getTournamentStartDate();
        Date endDate = tournament.getTournamentEndDate();
        List<Match> matches = new ArrayList<>();
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        // Generate all possible match combinations without assigning dates yet
        List<Match> allMatches = generateMatches(teamNames, teamNameIds, umpires, venues, tournament.getId());
        // Shuffle the matches
        Collections.shuffle(allMatches);
        addAdditionalMatches(allMatches, 3, tournament.getId(), umpires, venues);
        // Calculate the number of days between each match
        int totalMatches = allMatches.size();
        long daysBetweenMatches = diffInDays / totalMatches;
        // Assign dates and times to matches in a pattern
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Random random = new Random();
        for (Match match : allMatches) {
            // Assign a random time between 9:00 AM and 7:00 PM
            int hour = random.nextInt(10) + 9; // Random hour between 9 and 18 (7 PM)
            int minute = random.nextInt(60); // Random minute between 0 and 59
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            match.setMatchDate(calendar.getTime());
            matches.add(match);
            calendar.add(Calendar.DAY_OF_MONTH, (int) daysBetweenMatches);
        }
        matchService.saveAllMatches(matches);
        String createTableQuery = "CREATE TABLE IF NOT EXISTS PlayerStats (id INT AUTO_INCREMENT PRIMARY KEY, matchId INT, playerId INT, runs INT, wickets INT)";
        jdbcTemplate.execute(createTableQuery);
        int currentTournamentId = tournament.getId();
        List<Match> scheduledMatches = matchService.getAllMatchesByTournamentId(currentTournamentId);
        for (Match match : scheduledMatches) {
            List<Player> team1Players = playerService.getPlayersInfo(match.getTeam1id());
            List<Player> team2Players = playerService.getPlayersInfo(match.getTeam2id());
            for (Player player : team1Players) {
                String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, match.getId(), player.getId(), 0, 0);
            }
            // Insert data for team 2 players
            for (Player player : team2Players) {
                String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, match.getId(), player.getId(), 0, 0); // Set runs and wickets to 0 for team 2 players
            }
        }

        int updatedRows = entityManager.createNativeQuery(
                        "INSERT INTO NoofMatches (tournamentid, noofmatches) " +
                                "VALUES (?, ?) ")
                .setParameter(1, tournament.getId())
                .setParameter(2, matches.size())
                .executeUpdate();
        return "redirect:/";
    }

    private List<Match> generateMatches(List<String> teamNames, List<Integer> teamNameIds, List<String> umpires, List<String> venues, int tournamentId) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < teamNames.size(); i++) {
            for (int j = i + 1; j < teamNames.size(); j++) {
                Match match = new Match();
                match.setTournamentid(tournamentId);
                match.setTeam1(teamNames.get(i));
                match.setTeam2(teamNames.get(j));
                match.setTeam1id(teamNameIds.get(i));
                match.setTeam2id(teamNameIds.get(j));
                match.setUmpire(getRandomItemFromList(umpires));
                match.setVenue(getRandomItemFromList(venues));
                match.setComplete(false);
                matches.add(match);
            }
        }
        return matches;
    }

    private void addAdditionalMatches(List<Match> matches, int count, int tournamentId, List<String> umpires, List<String> venues) {
        for (int i = 0; i < count; i++) {
            Match match = new Match();
            match.setTournamentid(tournamentId);
            match.setTeam1("TBD");
            match.setTeam2("TBD");
            match.setTeam1id(0);
            match.setTeam2id(0);
            match.setUmpire(getRandomItemFromList(umpires));
            match.setVenue(getRandomItemFromList(venues));
            match.setComplete(false);
            matches.add(match);
        }
    }
    private String getRandomItemFromList(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));

    } @PersistenceContext
    private EntityManager entityManager;
    @GetMapping("/viewTournament")
    @Transactional
    public String displayTournament(@RequestParam("id") int id, Model model) {
        System.out.println("ID-> " + id);
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(id);
        if (!optionalTournament.isPresent()) {
            return "error1";
        }
        String sqlQuery = "INSERT INTO Viewer (tournamentid, noofviewers) " +
                "VALUES (?, 1) " +
                "ON DUPLICATE KEY UPDATE noofviewers = noofviewers + 1";
        int updatedRows = entityManager.createNativeQuery(sqlQuery)
                .setParameter(1, id)
                .executeUpdate();
        Schedule schedule = new Schedule();
        scheduleService.populateAllMatchesByTournamentid(schedule, id);
        Tournament tournament = optionalTournament.get();
        int x = 0;
        boolean allMatchesComplete = true;
        int lastMatchIndex = schedule.getListOfTotalMatches().size() - 1;
        if(schedule.getListOfTotalMatches().size()==0){
            return "error1";
        }
        for (int i = 0; i < lastMatchIndex - 2; i++) { // Iterate through all matches except last three
            Match match = schedule.getListOfTotalMatches().get(i);
            if (!match.isComplete()) {
                allMatchesComplete = false;
                break;
            }
            x = schedule.getListOfTotalMatches().get(i).getId();
        }
        Match lastScheduledMatch = schedule.getListOfTotalMatches().get(lastMatchIndex);
        if (lastScheduledMatch==null) {
            return "error1";
        }
        model.addAttribute("lastScheduledMatch", lastScheduledMatch.getWon());
        model.addAttribute("tournament", tournament);
        model.addAttribute("schedule", schedule);
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ha", Locale.ENGLISH);
        for (Match match : schedule.getListOfTotalMatches()) {
            formattedDates.add(dateFormat.format(match.getMatchDate()));
        }
        model.addAttribute("formattedDates", formattedDates);
        return "viewTournament";
    }

    @GetMapping("/editShedule1")
    public String editTournament(@RequestParam("id") int id, HttpServletRequest request, Model model) {
        Schedule schedule = new Schedule();
        scheduleService.populateAllMatchesByTournamentid(schedule, id);
        if(schedule.getListOfTotalMatches().size()==0)
        {
           return "error1";
        }
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(id);
        if (!optionalTournament.isPresent()) {
            return "error1"; // Or handle the absence of tournament data appropriately
        }
        Tournament tournament = optionalTournament.get();
        if (tournament.getUserId() == null || !tournament.getUserId().equals(request.getSession().getAttribute("userId"))) {
            return "error1"; // Or handle unauthorized access appropriately
        }
        model.addAttribute("tournament", tournament);
        model.addAttribute("schedule", schedule);
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ha", Locale.ENGLISH);
        for (Match match : schedule.getListOfTotalMatches()) {
            System.out.println(match);
            formattedDates.add(dateFormat.format(match.getMatchDate()));
        }
        model.addAttribute("formattedDates", formattedDates);
        return "editShedule";
    }

    @GetMapping("/editShedule2")
    public String EditShedule2(@RequestParam("matchId") int matchId,
                               @RequestParam("tournamentId") int tournamentId,
                               HttpServletRequest request,
                               Model model) {
        Match match = matchService.getMatchInfo(matchId);
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        if(match==null||match.getTeam1()=="TBD")
        {
            return "error1";
        }
        if (!optionalTournament.isPresent()) {
            return "error1";
        }
        Tournament tournament = optionalTournament.get();
        if (tournament.getUserId() == null || !tournament.getUserId().equals(request.getSession().getAttribute("userId"))) {
            return "error1";
        }
        ScoreCard scoreCard = scoreCardService.getScoreCardInfo(matchId);
        List<Player> team1Players = playerService.getPlayersInfo(match.getTeam1id());
        List<Player> team2Players = playerService.getPlayersInfo(match.getTeam2id());
        String team1Query = "SELECT runs, wickets FROM PlayerStats WHERE matchId = ? AND playerId IN (";
        for (int i = 0; i < team1Players.size(); i++) {
            team1Query += team1Players.get(i).getId();
            if (i < team1Players.size() - 1) {
                team1Query += ",";
            }
        }
        team1Query += ")";
        String team2Query = "SELECT runs, wickets FROM PlayerStats WHERE matchId = ? AND playerId IN (";
        for (int i = 0; i < team2Players.size(); i++) {
            team2Query += team2Players.get(i).getId();
            if (i < team2Players.size() - 1) {
                team2Query += ",";
            }
        }
        team2Query += ")";
        List<Map<String, Object>> team1Stats = jdbcTemplate.queryForList(team1Query, matchId);
        List<Map<String, Object>> team2Stats = jdbcTemplate.queryForList(team2Query, matchId);
        for (int i = 0; i < team1Players.size(); i++) {
            Map<String, Object> playerStats = team1Stats.get(i);
            int runs = (int) playerStats.get("runs");
            int wickets = (int) playerStats.get("wickets");
            // Update the corresponding player in team1Players
            team1Players.get(i).setRuns(runs);
            team1Players.get(i).setWickets(wickets);
        }
        for (int i = 0; i < team2Players.size(); i++) {
            Map<String, Object> playerStats = team2Stats.get(i);
            int runs = (int) playerStats.get("runs");
            int wickets = (int) playerStats.get("wickets");
            team2Players.get(i).setRuns(runs);
            team2Players.get(i).setWickets(wickets);
        }

        model.addAttribute("match", match);
        model.addAttribute("tournament", tournament);
        model.addAttribute("scoreCard", scoreCard);
        model.addAttribute("team1Players", team1Players);
        model.addAttribute("team2Players", team2Players);
        return "EditShedule2";
    }
    @GetMapping("/PointsTable")
    public String viewPointsTable(@RequestParam("id") int tournamentId, Model model) {
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        if (!optionalTournament.isPresent()) {
            return "error1";
        }
        List<Tournament.TeamName> teamNames = teamNameRepository.findByTournamentId(tournamentId);

        teamNames.sort(Comparator.comparingInt(Tournament.TeamName::getPoints)
                .thenComparingDouble(Tournament.TeamName::getNetRunRate)
                .reversed());
        Tournament tournament = optionalTournament.orElse(null);
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("tournament", tournament);
        return "PointsTable1";
    }

    @GetMapping("/Stats")
    public String viewStats(@RequestParam("id") int tournamentId, Model model) {
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        if (!optionalTournament.isPresent()) {
            return "error1";
        }
        String topRunScorersQuery = "SELECT p.* " +
                "FROM team_name tn " +
                "JOIN players p ON tn.id = p.team_id " +
                "WHERE tn.tournament_id = ? " +
                "ORDER BY p.total_runs DESC " +
                "LIMIT 5";

        String topWicketTakersQuery = "SELECT p.* " +
                "FROM team_name tn " +
                "JOIN players p ON tn.id = p.team_id " +
                "WHERE tn.tournament_id = ? " +
                "ORDER BY p.total_wickets DESC " +
                "LIMIT 5";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement runScorersStatement = connection.prepareStatement(topRunScorersQuery);
             PreparedStatement wicketTakersStatement = connection.prepareStatement(topWicketTakersQuery)) {
            runScorersStatement.setInt(1, tournamentId);
            wicketTakersStatement.setInt(1, tournamentId);
            ResultSet runScorersResultSet = runScorersStatement.executeQuery();
            ResultSet wicketTakersResultSet = wicketTakersStatement.executeQuery();
            List<Player> topRunScorers = new ArrayList<>();
            List<Player> topWicketTakers = new ArrayList<>();
            while (runScorersResultSet.next()) {
                Player player = extractPlayerInfo(runScorersResultSet);
                topRunScorers.add(player);
            }
            while (wicketTakersResultSet.next()) {
                Player player = extractPlayerInfo(wicketTakersResultSet);
                topWicketTakers.add(player);
            }
            Tournament tournament = optionalTournament.orElse(null);
            model.addAttribute("tournament", tournament);
            model.addAttribute("topRunScorers", topRunScorers);
            model.addAttribute("topWicketTakers", topWicketTakers);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Stats1";
    }

    private Player extractPlayerInfo(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int teamId = resultSet.getInt("team_id");
        String name = resultSet.getString("name");
        int runs = resultSet.getInt("runs");
        int totalRuns = resultSet.getInt("total_runs");
        int wickets = resultSet.getInt("wickets");
        int totalWickets = resultSet.getInt("total_wickets");
        Player player = new Player();
        player.setId(id);
        player.setTeamId(teamId);
        player.setName(name);
        player.setRuns(runs);
        player.setTotalRuns(totalRuns);
        player.setWickets(wickets);
        player.setTotalWickets(totalWickets);
        return player;
    }
    @GetMapping("generateSemiFinal")
    public String generateSemiFinal1(@RequestParam("tournamentId") int tournamentId, Model model) {
        Schedule schedule = new Schedule();
        int x = 0;
        scheduleService.populateAllMatchesByTournamentid(schedule, tournamentId);
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        Tournament tournament = optionalTournament.get(); // Throw exception if tournament is not found
        boolean allMatchesComplete = true;
        int lastMatchIndex = schedule.getListOfTotalMatches().size() - 1;
        for (int i = 0; i < lastMatchIndex - 2; i++) { // Iterate through all matches except last three
            Match match = schedule.getListOfTotalMatches().get(i);
            if (!match.isComplete()) {
                allMatchesComplete = false;
                break;
            }
            x = schedule.getListOfTotalMatches().get(i).getId();
        }
        if (allMatchesComplete) {
            List<Tournament.TeamName> top4Teams = new ArrayList<>();
            String sqlTop4Teams = "SELECT id, points, net_run_rate, team_name " +
                    "FROM team_name " +
                    "WHERE tournament_id = ? " +
                    "ORDER BY points DESC, net_run_rate DESC, team_name ASC " +
                    "LIMIT 4";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sqlTop4Teams)) {
                statement.setInt(1, tournamentId); // Set the tournament_id parameter
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int teamId = resultSet.getInt("id");
                        int points = resultSet.getInt("points");
                        double netRunRate = resultSet.getDouble("net_run_rate");
                        String teamName = resultSet.getString("team_name");
                        Tournament.TeamName team = new Tournament.TeamName();
                        team.setId(teamId);
                        team.setPoints(points);
                        team.setNetRunRate(netRunRate);
                        team.setTeamName(teamName);
                        top4Teams.add(team);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            updateMatch(top4Teams.get(0).getId(), top4Teams.get(0).getTeamName(),
                    top4Teams.get(3).getId(), top4Teams.get(3).getTeamName(),
                    tournamentId, 3);
            updateMatch(top4Teams.get(1).getId(), top4Teams.get(1).getTeamName(),
                    top4Teams.get(2).getId(), top4Teams.get(2).getTeamName(),
                    tournamentId, 2);
            x++;
            storePlayerStats(x, top4Teams.get(0).getId(), top4Teams.get(3).getId(), tournamentId);
            x++;
            storePlayerStats(x, top4Teams.get(1).getId(), top4Teams.get(2).getId(), tournamentId);
            return "redirect:/editShedule1?id=" + tournament.getId();
        } else {
            return "redirect:/editShedule1?id=" + tournament.getId() + "&error=Complete+All+Matches+First";
        }
    }

    private void updateMatch2(int team1Id, String team1Name, int team2Id, String team2Name, int matchIndex) {
        String sqlUpdateMatch = "UPDATE matches " +
                "SET team1id = ?, team1 = ?, team2id = ?, team2 = ? " +
                "WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdateMatch)) {
            statement.setInt(1, team1Id);
            statement.setString(2, team1Name);
            statement.setInt(3, team2Id);
            statement.setString(4, team2Name);
            statement.setInt(5, matchIndex);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the exception appropriately
        }
    }
    private void updateMatch(int team1Id, String team1Name, int team2Id, String team2Name, int tournamentId, int matchIndex) {
        String sqlUpdateMatch = "UPDATE matches " +
                "SET team1id = ?, team1 = ?, team2id = ?, team2 = ? " +
                "WHERE tournamentid = ? AND id = ( " +
                "    SELECT id FROM ( " +
                "        SELECT id FROM matches WHERE tournamentid = ? ORDER BY id DESC LIMIT ?, 1 " +
                "    ) AS temp " +
                ")";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdateMatch)) {
            statement.setInt(1, team1Id);
            statement.setString(2, team1Name);
            statement.setInt(3, team2Id);
            statement.setString(4, team2Name);
            statement.setInt(5, tournamentId);
            statement.setInt(6, tournamentId);
            statement.setInt(7, matchIndex - 1); // Adjusted index for SQL's 0-based indexing
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void storePlayerStats(int matchId, int team1Id, int team2Id, int tournamentId) {
        List<Player> team1Players = playerService.getPlayersInfo(team1Id);
        List<Player> team2Players = playerService.getPlayersInfo(team2Id);
        for (Player player : team1Players) {
            String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, matchId, player.getId(), 0, 0);
        }
        for (Player player : team2Players) {
            String insertQuery = "INSERT INTO PlayerStats (matchId, playerId, runs, wickets) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, matchId, player.getId(), 0, 0);
        }
    }

   /* @GetMapping("/tournamentsPerMonth")
    public String tournamentsPerMonth(Model model) {
        Map<String, Integer> tournamentsPerMonth = tservice.getTournamentsPerMonth();
        model.addAttribute("tournamentsPerMonth", tournamentsPerMonth);
        return "tournamentsPerMonth";
    }*/
   @GetMapping("/tournamentsPerMonth")
   public String getTournamentsPerMonth(Model model) {
       Map<String, Integer> tournamentsPerMonth = tservice.getTournamentsPerMonth();
       model.addAttribute("graphData", tournamentsPerMonth); // Corrected attribute name
       return "tournamentsPerMonth1";
   }

    @GetMapping("/viewersPerTournament")
    public String getViewersperMont(Model model) {
        // 1. Write a query to fetch data from the Viewer table
        String sqlQuery = "SELECT tournamentid, noofviewers FROM Viewer";
        // 2. Execute the query and process the result
        List<Object[]> result = entityManager.createNativeQuery(sqlQuery).getResultList();
        Map<String, Integer> viewersPerTournament = new HashMap<>();
        for (Object[] row : result) {
            int tournamentId = (Integer) row[0];
            int numberOfViewers = (Integer) row[1];
            // Fetch tournament name based on tournament ID
            Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
            if (optionalTournament.isPresent()) {
                String tournamentName = optionalTournament.get().getNameOfTournament();
                viewersPerTournament.put(tournamentName, numberOfViewers);
            }
        }
        // 3. Pass the data to the HTML page through the model
        model.addAttribute("viewersPerTournament", viewersPerTournament);
        return "ViewersPerTournament2";
    }

    @GetMapping("/matchesPerTournament")
    public String getMatchesPerTournament(Model model) {
        // 1. Write a query to fetch data from the NoofMatches table
        String sqlQuery = "SELECT tournamentid, noofmatches FROM NoofMatches";
        // 2. Execute the query and process the result
        List<Object[]> result = entityManager.createNativeQuery(sqlQuery).getResultList();
        Map<String, Integer> matchesPerTournament = new HashMap<>();
        for (Object[] row : result) {
            int tournamentId = (Integer) row[0];
            int numberOfMatches = (Integer) row[1];
            // Fetch tournament name based on tournament ID
            Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
            if (optionalTournament.isPresent()) {
                String tournamentName = optionalTournament.get().getNameOfTournament();
                matchesPerTournament.put(tournamentName, numberOfMatches);
            }
        }
        // 3. Pass the data to the HTML page through the model
        model.addAttribute("matchesPerTournament", matchesPerTournament);
        return "MatchesPerTournament2";
    }




  /*  @GetMapping("/getTournamentsPerMonthData")
    @ResponseBody
    public List<Map<String, Object>> getTournamentsPerMonthData() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Integer> tournamentsPerMonth = tservice.getTournamentsPerMonth();
        tournamentsPerMonth.forEach((month, count) -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", month);
            entry.put("count", count);
            data.add(entry);
        });
        return data;
    }*/

}