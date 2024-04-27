package com.TournamentShedulingSystem.TournamentManagement.MatchManagement;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getTournamentid() {
        return tournamentid;
    }

    public void setTournamentid(int tournamentid) {
        this.tournamentid = tournamentid;
    }

    private int tournamentid;
    private String team1;
    private String team2;
    private int team1id;
    private int team2id;
    private String highlights;
    private double netRunRate;
    private List<String> comments;
    private String umpire;
    private String venue;

    public String getWon() {
        return Won;
    }

    public void setWon(String won) {
        Won = won;
    }

    private String Won;
    private boolean Complete;


    public boolean isComplete() {
        return Complete;
    }

    public void setComplete(boolean complete) {
        Complete = complete;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public int getTeam1id() {
        return team1id;
    }

    public void setTeam1id(int team1id) {
        this.team1id = team1id;
    }

    public int getTeam2id() {
        return team2id;
    }

    public void setTeam2id(int team2id) {
        this.team2id = team2id;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    private Date matchDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }


    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public double getNetRunRate() {
        return netRunRate;
    }

    public void setNetRunRate(double netRunRate) {
        this.netRunRate = netRunRate;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getUmpire() {
        return umpire;
    }

    public void setUmpire(String umpire) {
        this.umpire = umpire;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", tournamentid=" + tournamentid +
                ", team1id=" + team1id +
                ", team2id=" + team2id +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", umpire='" + umpire + '\'' +
                ", venue='" + venue + '\'' +
                ", matchDate=" + matchDate +
                '}';
    }
}
