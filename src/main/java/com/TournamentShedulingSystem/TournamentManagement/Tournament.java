package com.TournamentShedulingSystem.TournamentManagement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Tournament")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Long UserId;
    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    private int noOfTeams;
    @DateTimeFormat(pattern = "d-M-yy") // Format to display only the date in the pattern 2-3-44
    private Date tournamentStartDate;
    @DateTimeFormat(pattern = "d-M-yy") // Format to display only the date in the pattern 2-3-44
    private Date tournamentEndDate;
    private int tournamentDuration;
    private String nameOfTournament;
    private String typeOfTournament;

    public List<String> getVenues() {
        List<String> venueNames = new ArrayList<>();
        for (Venue venue : venues) {
            venueNames.add(venue.getVenueName());
        }
        return venueNames;
    }

    public List<String> getUmpires() {
        List<String> umpireNames = new ArrayList<>();
        for (Umpire umpire : umpires) {
            umpireNames.add(umpire.getUmpireName());
        }
        return umpireNames;
    }

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Venue> venues;
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Umpire> umpires;
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TeamName> teamNames;
    public int getNoOfTeams() {
        return noOfTeams;
    }
    public void setNoOfTeams(int noOfTeams) {
        this.noOfTeams = noOfTeams;
    }
    public Date getTournamentStartDate() {
        return tournamentStartDate;
    }
    public void setTournamentStartDate(Date tournamentStartDate) {
        this.tournamentStartDate = tournamentStartDate;
    }
    public Date getTournamentEndDate() {
        return tournamentEndDate;
    }
    public void setTournamentEndDate(Date tournamentEndDate) {
        this.tournamentEndDate = tournamentEndDate;
    }
    public int getTournamentDuration() {
        return tournamentDuration;
    }
    public void setTournamentDuration(int tournamentDuration) {
        this.tournamentDuration = tournamentDuration;
    }
    public String getNameOfTournament() {
        return nameOfTournament;
    }
    public void setNameOfTournament(String nameOfTournament) {
        this.nameOfTournament = nameOfTournament;
    }
    public String getTypeOfTournament() {
        return typeOfTournament;
    }
    public void setTypeOfTournament(String typeOfTournament) {
        this.typeOfTournament = typeOfTournament;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public void setVenues(List<Venue> venues) {
        this.venues = venues;
        for (Venue venue : venues) {
            venue.setTournament(this);
        }
    }
    public void setUmpires(List<Umpire> umpires) {
        this.umpires = umpires;
        for (Umpire umpire : umpires) {
            umpire.setTournament(this);
        }
    }
    public void setTeamNames(List<TeamName> teamNames) {
        this.teamNames = teamNames;
        for (TeamName teamName : teamNames) {
            teamName.setTournament(this);
        }

}
    public List<String> getTeamNames() {
        List<String> teamNames = new ArrayList<>();
        if (teamNames != null) {
            for (TeamName teamName : this.teamNames) {
                teamNames.add(teamName.getTeamName());
            }
        }
        return teamNames;
    }
    public List<Integer> getTeamNameIds() {
        List<Integer> teamNameIds = new ArrayList<>();
        if (this.teamNames != null) {
            for (TeamName teamName : this.teamNames) {
                teamNameIds.add(teamName.getId());
            }
        }
        return teamNameIds;
    }

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Venue")
public static class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    private String venueName;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public Tournament getTournament() {
            return tournament;
        }
        public void setTournament(Tournament tournament) {
            this.tournament = tournament;
        }
        public String getVenueName() {
            return venueName;
        }
        public void setVenueName(String venueName) {
            this.venueName = venueName;
        }
}
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "Umpire")
    public static class Umpire {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        @ManyToOne
        @JoinColumn(name = "tournament_id")
        private Tournament tournament;
        private String umpireName;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public Tournament getTournament() {
            return tournament;
        }
        public void setTournament(Tournament tournament) {
            this.tournament = tournament;
        }
        public String getUmpireName() {
            return umpireName;
        }
        public void setUmpireName(String umpireName) {
            this.umpireName = umpireName;
        }
}
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "TeamName")
    public static class TeamName {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        @ManyToOne
        @JoinColumn(name = "tournament_id")
        private Tournament tournament;
        private String teamName;

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        private int points;

        public double getNetRunRate() {
            return NetRunRate;
        }

        public void setNetRunRate(double netRunRate) {
            NetRunRate = netRunRate;
        }

        private double NetRunRate;
        public Tournament getTournament() {
            return tournament;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public void setTournament(Tournament tournament) {
            this.tournament = tournament;
        }
        public String getTeamName() {
            return teamName;
        }
        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

    }
}