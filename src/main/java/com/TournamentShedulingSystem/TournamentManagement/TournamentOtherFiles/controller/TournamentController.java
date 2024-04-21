package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.controller;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles.PlayerService;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentForm;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.service.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
@Controller
public class TournamentController {
    private TournamentService tservice;
    private  PlayerService playerService;
    @Autowired
    public TournamentController(TournamentService tservice,PlayerService playerService) {
        this.tservice = tservice;
        this.playerService = playerService;
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
        // Invalidate session to clear user data
        HttpSession session = request.getSession();
        session.invalidate();

        // Redirect to the home page
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
    public String listTournament(Model model) {
        List<Tournament> tournaments = tservice.findAllTournaments();

        for (Tournament tournament : tournaments) {
            System.out.println("Tournament ID: " + tournament.getId());
            System.out.println("Tournament Name: " + tournament.getNameOfTournament());
            System.out.println("Number of Teams: " + tournament.getNoOfTeams());
            System.out.println("Tournament Duration: " + tournament.getTournamentDuration());
            System.out.println("Tournament Start Date: " + tournament.getTournamentStartDate());
            System.out.println("Tournament End Date: " + tournament.getTournamentEndDate());
            System.out.println("Type of Tournament: " + tournament.getTypeOfTournament());
            System.out.println("---------------------------------------------");
        }
        model.addAttribute("tournament", tournaments);
        return "Tournament-list";
    }

    @GetMapping("/tournamentnew")
    public String createTournament(Model model, HttpServletRequest request) {
        // Check if the user is authenticated before allowing access to the page
        if (request.getSession().getAttribute("userId") == null) {
            return "redirect:/signup";
        } else {

            model.addAttribute("tournamentForm", new TournamentForm());
            return "CreatingTournament/creating-Tournament";
        }
    }

    @PostMapping("/tournamentnew")
    public String processTournamentForm(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, HttpSession session, HttpServletRequest request) {
        // Set the tournamentForm object in the session
        session.setAttribute("tournamentForm", tournamentForm);
//        System.out.println(request.getSession().getAttribute("userId"));
        // Print the form data
        System.out.println("No of teams: " + tournamentForm.getNoOfTeams());
        System.out.println("Tournament Start Date: " + tournamentForm.getTournamentStartDate());
        System.out.println("Tournament End Date: " + tournamentForm.getTournamentEndDate());
        System.out.println("Tournament Duration: " + tournamentForm.getTournamentDuration());
        System.out.println("Name of Tournament: " + tournamentForm.getNameOfTournament());
        System.out.println("No of Venues: " + tournamentForm.getNoOfVenues());
        System.out.println("No of Umpires: " + tournamentForm.getNoOfUmpires());

        // Redirect to the next form
        return "redirect:/tournament2";
    }

    @GetMapping("/tournament2")
    public String showTournament2Form(HttpSession session, Model model) {
        TournamentForm tournamentForm = (TournamentForm) session.getAttribute("tournamentForm");
        if (tournamentForm == null) {
            // Redirect to the first form if no data is available
            return "redirect:/tournamentnew";
        }

        // Check if tournamentForm.noOfTeams is a power of 4
        if (isPowerOfFour(tournamentForm.getNoOfTeams()) && isPowerOfTwo(tournamentForm.getNoOfTeams())) {
            model.addAttribute("tournamentForm", tournamentForm);
            return "CreatingTournament/creating-Tournament3";
        } else if (isPowerOfFour(tournamentForm.getNoOfTeams())) {
            model.addAttribute("tournamentForm", tournamentForm);
            return "CreatingTournament/creating-Tournament1";
        } else if (isPowerOfTwo(tournamentForm.getNoOfTeams())) {
            model.addAttribute("tournamentForm", tournamentForm);
            return "CreatingTournament/creating-Tournament2";
        } else {
            model.addAttribute("tournamentForm", tournamentForm);
            return "CreatingTournament/creating-Tournament4";
        }
    }

    private boolean isPowerOfFour(Integer number) {
        if (number == null || number <= 0) {
            return false;
        }
        // Calculate the logarithm base 4 of the number
        double logResult = Math.log(number) / Math.log(4);
        // Check if the result is an integer
        return Math.floor(logResult) == logResult;
    }

    private boolean isPowerOfTwo(Integer number) {
        if (number == null || number <= 0) {
            return false;
        }
        // Calculate the logarithm base 2 of the number
        double logResult = Math.log(number) / Math.log(2);
        // Check if the result is an integer
        return Math.floor(logResult) == logResult;
    }

    @PostMapping("/tournament2")
    public String saveTournament(@ModelAttribute("tournamentForm") TournamentForm tournamentForm, HttpSession session, HttpServletRequest request, Model model) {
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

        model.addAttribute("tournament", tournament);
        return "CreatingTournament/creating-Tournament5";
    }
    @PostMapping("/tournament3")
    public String submitFormDetails( HttpServletRequest request,HttpSession session, Model model) {
        Tournament tournament = (Tournament) session.getAttribute("tournament");
        List<Integer> teamNameIds = tournament.getTeamNameIds();
        Enumeration<String> parameterNames = request.getParameterNames();
        int count=0;
        int x=0;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            // Print parameter name and its values
//            System.out.println("Parameter: " + paramName);
            for (String value : paramValues) {
//                System.out.println("Value: " + value);
                String playerName =value;
                Player player = new Player();
         if(count%11==0&&count>0){
             x++;
         }
                player.setTeamId(teamNameIds.get(x));
                player.setName(playerName);
                player.setRuns(0);
                player.setTotalRuns(0);
                player.setWickets(0);
                player.setTotalWickets(0);
                playerService.savePlayer(player);
                count++;

            }
        }
        model.addAttribute("tournament", tournament);
        return"ShedulingTournament/ShedulingTournament1";
    }
@PostMapping("/schedule1")
public String ScheduleDetails( HttpServletRequest request,HttpSession session, Model model) {
    Tournament tournament = (Tournament) session.getAttribute("tournament");
    return"redirect:/";
}

}