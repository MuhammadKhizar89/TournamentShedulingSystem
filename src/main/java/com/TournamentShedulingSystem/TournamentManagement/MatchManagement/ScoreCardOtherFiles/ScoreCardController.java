package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCardOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles.CommentsService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles.MatchService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles.PlayerService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCard;
import com.TournamentShedulingSystem.TournamentManagement.Schedule;
import com.TournamentShedulingSystem.TournamentManagement.ScheduleOtherFiles.ScheduleService;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TeamNameRepository;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentService;
import com.TournamentShedulingSystem.UserManagement.Actor;
import com.TournamentShedulingSystem.UserManagement.ActorOtherFiles.ActorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
@Controller
public class ScoreCardController {
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
    public ScoreCardController(TournamentService tservice, PlayerService playerService, MatchService matchService, ScheduleService scheduleService, ScoreCardService scoreCardService, TeamNameRepository teamNameRepository, CommentsService commentsService, ActorService actorService) {
        this.tservice = tservice;
        this.playerService = playerService;
        this.matchService = matchService;
        this.scheduleService = scheduleService;
        this.scoreCardService = scoreCardService;
        this.teamNameRepository = teamNameRepository;
        this.commentsService = commentsService;
        this.actorService = actorService;
    }

    @PostMapping("/submitScoreCard")
    public String editScoreCard(HttpServletRequest request,
                                @RequestParam("tournamentId") int tournamentId,
                                @RequestParam("matchId") int matchId,
                                @RequestParam("file") MultipartFile file,
                                Model model) {
        Enumeration<String> parameterNames = request.getParameterNames();
        Match match = matchService.getMatchInfo(matchId);
        ArrayList<Integer> team1PlayersIndivisual = new ArrayList<>();
        ArrayList<Integer> team2PlayersIndivisual = new ArrayList<>();
        ArrayList<Integer> team1PlayersIndivisualWickets = new ArrayList<>();
        ArrayList<Integer> team2PlayersIndivisualWickets = new ArrayList<>();
        List<Player> team1Players = playerService.getPlayersInfo(match.getTeam1id());
        List<Player> team2Players = playerService.getPlayersInfo(match.getTeam2id());
        System.out.println("Team1" + team1Players);
        int x = 1;
        int team1Runs = 0;
        int team1Wickets = 0;
        int team2Runs = 0;
        int team2Wickets = 0;
        int team1Overs = 0;
        int team2Overs = 0;
        int count = 0;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String value : paramValues) {
                if (x == 1) {
                    team1Overs = Integer.parseInt(value);
                } else if (x == 2) {
                    team2Overs = Integer.parseInt(value);
                } else if (x > 2 && x < 14) {
                    team1PlayersIndivisual.add(Integer.parseInt(value));
                    System.out.println(count);
                    count++;
                    team1Runs = team1Runs + Integer.parseInt(value);
                } else if (x > 13 && x < 25) {
                    team1PlayersIndivisualWickets.add(Integer.parseInt(value));
                    team1Wickets = team1Wickets + Integer.parseInt(value);
                } else if (x > 24 && x < 36) {
                    team2PlayersIndivisual.add(Integer.parseInt(value));
                    team2Runs = team2Runs + Integer.parseInt(value);
                } else if (x > 35 && x < 47) {
                    team2PlayersIndivisualWickets.add(Integer.parseInt(value));
                    team2Wickets = team2Wickets + Integer.parseInt(value);
                }
                x++;
            }
        }
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        Tournament tournament = optionalTournament.get();
        if (tournament.getTournamentDuration() < team1Overs || tournament.getTournamentDuration() < team2Overs) {
            return "redirect:/editShedule2?matchId=" + matchId + "&tournamentId=" + tournamentId + "&error=Enter Overs In the Range " + tournament.getTournamentDuration() + "Overs";
        }
        if (team1Wickets > 10) {
            return "redirect:/editShedule2?matchId=" + matchId + "&tournamentId=" + tournamentId + "&errorwicket1=Enter Wickets In the Range 10 ";
        }
        if (team2Wickets > 10) {
            return "redirect:/editShedule2?matchId=" + matchId + "&tournamentId=" + tournamentId + "&errorwicket2=Enter Wickets In the Range 10 ";
        }
        if (!file.isEmpty()) {
            try {
                // Generate a unique file name based on match ID and file extension
                String fileName = matchId + getFileExtension(file.getOriginalFilename());
                // Get the path where you want to save the file
                Path uploadDir = Paths.get("src/main/resources/static/Highlights/");
                // Create the directory if it doesn't exist
                Files.createDirectories(uploadDir);
                // Resolve the path for the uploaded file
                Path filePath = uploadDir.resolve(fileName);
                // Copy the file to the specified directory
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle file upload error
                return "redirect:/editShedule2?matchId=" + matchId + "&tournamentId=" + tournamentId + "&errorfile1=fileUploadError";
            }
        }
        // Update team 1 players
        int i = 0;
        for (Player player : team1Players) {
            int newTotalRuns = player.getTotalRuns() + team1PlayersIndivisual.get(i);
            player.setTotalRuns(newTotalRuns);
            int newTotalWickets = player.getTotalWickets() + team1PlayersIndivisualWickets.get(i);
            player.setTotalWickets(newTotalWickets);
            playerService.addPlayerInfo(player);
            i++;

        }
        i = 0;
        for (Player player : team2Players) {
            int newTotalRuns = player.getTotalRuns() + team2PlayersIndivisual.get(i);
            player.setTotalRuns(newTotalRuns);
            int newTotalWickets = player.getTotalWickets() + team2PlayersIndivisualWickets.get(i);
            player.setTotalWickets(newTotalWickets);
            playerService.addPlayerInfo(player);
            i++;
        }
        // Prepare the SQL UPDATE statement
        String sqlUpdate = "UPDATE PlayerStats SET runs = ?, wickets = ? WHERE playerId = ? AND matchId = ?";
        try (Connection connection = dataSource.getConnection()) {
            // Update team 1 players
            try (PreparedStatement statement1 = connection.prepareStatement(sqlUpdate)) {
                for (int i1 = 0; i1 < team1Players.size(); i1++) {
                    statement1.setInt(1, team1PlayersIndivisual.get(i1)); // Runs
                    statement1.setInt(2, team1PlayersIndivisualWickets.get(i1)); // Wickets
                    statement1.setInt(3, team1Players.get(i1).getId()); // Player ID
                    statement1.setInt(4, matchId); // Match ID
                    statement1.executeUpdate();
                }
            }
            // Update team 2 players
            try (PreparedStatement statement2 = connection.prepareStatement(sqlUpdate)) {
                for (int i1 = 0; i1 < team2Players.size(); i1++) {
                    statement2.setInt(1, team2PlayersIndivisual.get(i1)); // Runs
                    statement2.setInt(2, team2PlayersIndivisualWickets.get(i1)); // Wickets
                    statement2.setInt(3, team2Players.get(i1).getId()); // Player ID
                    statement2.setInt(4, matchId); // Match ID
                    statement2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sqlInsert = "INSERT INTO score_card (total_overs, matchid, team1score,team1wickets , team1_overs, team2score, team2wickets, team2_overs) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_overs = VALUES(total_overs), " +
                "team1score = VALUES(team1score), " +
                "team2wickets = VALUES(team1wickets), " + // Interchanged
                "team1_overs = VALUES(team1_overs), " +
                "team2score = VALUES(team2score), " +
                "team1wickets = VALUES(team2wickets), " + // Interchanged
                "team2_overs = VALUES(team2_overs)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
            statement.setInt(1, tournament.getTournamentDuration());  // total_overs
            statement.setInt(2, matchId);                              // matchid
            statement.setInt(3, team1Runs);                            // team1score
            statement.setInt(4, team2Wickets);                         // team1wickets
            statement.setFloat(5, team1Overs);                         // team1_overs
            statement.setInt(6, team2Runs);                            // team2score
            statement.setInt(7, team1Wickets);                         // team2wickets
            statement.setFloat(8, team2Overs);                         // team2_overs
            // Execute the INSERT statement
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Schedule schedule = new Schedule();
        scheduleService.populateAllMatchesByTournamentid(schedule, tournamentId);
        int lastMatchIndex = schedule.getListOfTotalMatches().size() - 1;
        boolean isLastThreeMatches = false;
        for (int ki = lastMatchIndex; ki >= Math.max(0, lastMatchIndex - 2); ki--) {
            if (matchId == schedule.getListOfTotalMatches().get(ki).getId()) {
                isLastThreeMatches = true;
                break;
            }
        }
        double teamnnr2 = team2Runs / team2Overs;
        double teamnnr20 = team1Runs / team1Overs;
        double teamnnr1 = team1Runs / team1Overs;
        double teamnnr10 = team2Runs / team2Overs;
        double nrr1 = (teamnnr1 - teamnnr10);
        double nrr2 = (teamnnr2 - teamnnr20);
        boolean isLastMatch = false;
        if (matchId == schedule.getListOfTotalMatches().get(lastMatchIndex).getId()) {
            isLastMatch = true;
        }
        String sqlUpdate2 = "UPDATE matches SET complete = 1, won = ? WHERE id = ?";
        String won;
        if (team1Runs > team2Runs) {
            won = match.getTeam1();
        } else if (team1Runs < team2Runs) {
            won = match.getTeam2();
        } else {
            won = "tied";
        }
        if (isLastThreeMatches) {
            if (won == "tied" && isLastMatch) {
                won = "tied";
            } else if (won == "tied") {
                if (nrr1 > nrr2) {
                    won = match.getTeam1();
                } else {
                    won = match.getTeam2();
                }
            }
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdate2)) {
            statement.setString(1, won);   // won
            statement.setInt(2, matchId);  // matchId
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sqlUpdate1 = "UPDATE matches SET complete = 1 WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdate1)) {
            // Set parameter for the prepared statement
            statement.setInt(1, matchId);  // matchId
            // Execute the UPDATE statement
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int points1 = 0;
        int points2 = 0;
        if (Objects.equals(won, match.getTeam1())) {
            points1 = 2;
        } else if (Objects.equals(won, match.getTeam2())) {
            points2 = 2;
        } else {
            points1 = 1;
            points2 = 1;
        }
        boolean isSecondLastMatch = false;
        int secondLastMatchIndex = Math.max(0, lastMatchIndex - 1);
        for (int ki = lastMatchIndex; ki >= Math.max(0, lastMatchIndex - 2); ki--) {
            if (matchId == schedule.getListOfTotalMatches().get(ki).getId()) {
                if (ki == secondLastMatchIndex) {
                    isSecondLastMatch = true;
                }
                break;
            }
        }
        lastMatchIndex = schedule.getListOfTotalMatches().size() - 1;
        if (isSecondLastMatch) {
            Match thirdLastMatch = schedule.getListOfTotalMatches().get(lastMatchIndex - 2);
            Match secondLastMatch = schedule.getListOfTotalMatches().get(lastMatchIndex - 1);
            Match lastMatch = schedule.getListOfTotalMatches().get(lastMatchIndex);
            // Determine the winning teams for the third last and second last matches
            int winningTeam3rdLast = 0;
            int winningTeam2ndLast = 0;
            secondLastMatch.setWon(won);
            if (thirdLastMatch.getWon().equals(thirdLastMatch.getTeam1())) {
                winningTeam3rdLast = thirdLastMatch.getTeam1id();
            } else if (thirdLastMatch.getWon().equals(thirdLastMatch.getTeam2())) {
                winningTeam3rdLast = thirdLastMatch.getTeam2id();
            }
            if (secondLastMatch.getWon().equals(secondLastMatch.getTeam1())) {
                winningTeam2ndLast = secondLastMatch.getTeam1id();
            } else if (secondLastMatch.getWon().equals(secondLastMatch.getTeam2())) {
                winningTeam2ndLast = secondLastMatch.getTeam2id();
            }
            updateMatch2(winningTeam3rdLast, thirdLastMatch.getWon(),
                    winningTeam2ndLast, secondLastMatch.getWon(),
                    lastMatch.getId());
            storePlayerStats(lastMatch.getId(), winningTeam3rdLast, winningTeam2ndLast, tournamentId);
        }
        if (isLastThreeMatches) {
            nrr1 = 0;
            nrr2 = 0;
            points1 = 0;
            points2 = 0;
        }
        // Update for Team 1
        String sqlUpdateTeam1 = "UPDATE team_name SET points = points + ?, net_run_rate =net_run_rate+ ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdateTeam1)) {
            // Set parameters for the prepared statement
            statement.setInt(1, points1);   // points for Team 1
            statement.setDouble(2, nrr1);   // net run rate for Team 1
            statement.setInt(3, match.getTeam1id());  // team2Id
            // Execute the UPDATE statement
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Update for Team 2
        String sqlUpdateTeam2 = "UPDATE team_name SET points = points + ?, net_run_rate =net_run_rate+ ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlUpdateTeam2)) {
            // Set parameters for the prepared statement
            statement.setInt(1, points2);   // points for Team 2
            statement.setDouble(2, nrr2);   // net run rate for Team 2
            statement.setInt(3, match.getTeam2id());  // team2Id
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/editShedule1?id=" + tournamentId;
}

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
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
    @GetMapping("/ViewScoreCard")
    public String displayScoreCard(@RequestParam("matchId") int matchId,
                                   @RequestParam("tournamentId") int tournamentId,
                                   Model model) {
        Match match = matchService.getMatchInfo(matchId);
        Optional<Tournament> optionalTournament = tservice.getTournamentInfo(tournamentId);
        if (!optionalTournament.isPresent()) {
            return "error1";
        }
        Tournament tournament = optionalTournament.get();
        ScoreCard scoreCard = scoreCardService.getScoreCardInfo(matchId);
        List<Player> team1Players = playerService.getPlayersInfo(match.getTeam1id());
        List<Player> team2Players = playerService.getPlayersInfo(match.getTeam2id());
        if(match.isComplete()==false)
        {
            return "error1";
        }
        String team1Query = "SELECT runs, wickets FROM PlayerStats WHERE matchId = ? AND playerId IN (";
        for (int i = 0; i < team1Players.size(); i++) {
            team1Query += team1Players.get(i).getId();
            if (i < team1Players.size() - 1) {
                team1Query += ",";
            }
        }
        team1Query += ")";
        // SQL query to get runs and wickets for team 2 players
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
// Update team1Players with runs and wickets data
        for (int i = 0; i < team1Players.size(); i++) {
            Map<String, Object> playerStats = team1Stats.get(i);
            int runs = (int) playerStats.get("runs");
            int wickets = (int) playerStats.get("wickets");
            // Update the corresponding player in team1Players
            team1Players.get(i).setRuns(runs);
            team1Players.get(i).setWickets(wickets);
        }
// Update team2Players with runs and wickets data
        for (int i = 0; i < team2Players.size(); i++) {
            Map<String, Object> playerStats = team2Stats.get(i);
            int runs = (int) playerStats.get("runs");
            int wickets = (int) playerStats.get("wickets");
            team2Players.get(i).setRuns(runs);
            team2Players.get(i).setWickets(wickets);
        }
        List<Comments> comments = commentsService.getComments(match.getId());
        List<Actor> commentUsers = new ArrayList<>();
// Iterate through each comment to fetch the user details
        for (Comments comment : comments) {
            long userId = (long) comment.getUserid();
            Actor user = actorService.findByid(userId);
            commentUsers.add(user);
        }
        model.addAttribute("comments", comments);
        model.addAttribute("commentUsers", commentUsers);
        model.addAttribute("match", match);
        model.addAttribute("tournament", tournament);
        model.addAttribute("scoreCard", scoreCard);
        model.addAttribute("team1Players", team1Players);
        model.addAttribute("team2Players", team2Players);
        return "viewScoreCard1";
    }
}
