package com.TournamentShedulingSystem.TournamentManagement.MatchManagement;
import jakarta.persistence.*;
@Entity
@Table(name = "players")
public class Player {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getWickets() {
        return wickets;
    }

    public void setWickets(int wickets) {
        this.wickets = wickets;
    }

    public int getTotalWickets() {
        return totalWickets;
    }

    public void setTotalWickets(int totalWickets) {
        this.totalWickets = totalWickets;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int teamId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int runs;

    @Column(nullable = false)
    private int totalRuns;

    @Column(nullable = false)
    private int wickets;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", name='" + name + '\'' +
                ", runs=" + runs +
                ", totalRuns=" + totalRuns +
                ", wickets=" + wickets +
                ", totalWickets=" + totalWickets +
                '}';
    }

    @Column(nullable = false)
    private int totalWickets;
}
