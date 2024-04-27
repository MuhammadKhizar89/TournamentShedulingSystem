package com.TournamentShedulingSystem.TournamentManagement.MatchManagement;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "Comments")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String comment;
    private int matchId;
    private long userid;
    public long getUserid() {
        return userid;
    }
    public void setUserid(long userid) {
        this.userid = userid;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getMatchId() {
        return matchId;
    }
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}
