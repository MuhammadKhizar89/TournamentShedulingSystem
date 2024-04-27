package com.TournamentShedulingSystem.TournamentManagement.MatchManagement;
import jakarta.persistence.*;
@Entity
@Table(name = "ScoreCard") // Specify the table name
public class ScoreCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Override
    public String toString() {
        return "ScoreCard{" +
                "id=" + id +
                ", team1Score=" + team1Score +
                ", team2Score=" + team2Score +
                ", team1Wickets=" + team1Wickets +
                ", team2Wickets=" + team2Wickets +
                ", team1_Overs=" + team1_Overs +
                ", team2_Overs=" + team2_Overs +
                ", matchID=" + matchID +
                ", TotalOvers=" + TotalOvers +
                '}';
    }

    private int team1Score;
    private int team2Score;
    private int team1Wickets;
    private int team2Wickets;
    private float team1_Overs;
    private float team2_Overs;
    private int matchID;
    private int TotalOvers;
    public int getTotalOvers() {
        return TotalOvers;
    }
    public void setTotalOvers(int totalOvers) {
        TotalOvers = totalOvers;
    }

    public int getTeam1Wickets() {
        return team1Wickets;
    }

    public void setTeam1Wickets(int team1Wickets) {
        this.team1Wickets = team1Wickets;
    }

    public int getTeam2Wickets() {
        return team2Wickets;
    }

    public void setTeam2Wickets(int team2Wickets) {
        this.team2Wickets = team2Wickets;
    }

    public float getTeam1_Overs() {
        return team1_Overs;
    }

    public void setTeam1_Overs(float team1_Overs) {
        this.team1_Overs = team1_Overs;
    }

    public float getTeam2_Overs() {
        return team2_Overs;
    }

    public void setTeam2_Overs(float team2_Overs) {
        this.team2_Overs = team2_Overs;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

    public int getMatchID() {
        return matchID;
    }

    public void setMatchID(int matchID) {
        this.matchID = matchID;
    }
}
