package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles.CommentsService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles.PlayerService;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCardOtherFiles.ScoreCardService;
import com.TournamentShedulingSystem.TournamentManagement.ScheduleOtherFiles.ScheduleService;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TeamNameRepository;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentService;
import com.TournamentShedulingSystem.UserManagement.ActorOtherFiles.ActorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@Controller
public class MatchController {
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
    public MatchController(TournamentService tservice, PlayerService playerService, MatchService matchService, ScheduleService scheduleService, ScoreCardService scoreCardService, TeamNameRepository teamNameRepository, CommentsService commentsService, ActorService actorService) {
        this.tservice = tservice;
        this.playerService = playerService;
        this.matchService = matchService;
        this.scheduleService = scheduleService;
        this.scoreCardService = scoreCardService;
        this.teamNameRepository = teamNameRepository;
        this.commentsService = commentsService;
        this.actorService = actorService;
    }
    @PostMapping("/submitComment")
    public String addComment(@RequestParam("matchId") int matchId,
                                @RequestParam("tournamentId") int tournamentId,
                                @RequestParam("comment") String comment,
                                Model model,
                                HttpServletRequest request) { // Add HttpServletRequest parameter
        if (request.getSession().getAttribute("userId") != null) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            Comments newComment = new Comments();
            newComment.setMatchId(matchId);
            newComment.setComment(comment);
            newComment.setUserid(userId);
            commentsService.addComment(newComment);
        } else {
            return "signup_Page";
        }
        return "redirect:/ViewScoreCard?matchId=" + matchId + "&tournamentId=" + tournamentId;
    }
    private void editMatch(int team1Id, String team1Name, int team2Id, String team2Name, int matchIndex) {
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
}
