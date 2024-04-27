package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;
import java.util.List;
public class TournamentForm {
    public int getNoOfTeams() {
        return noOfTeams;
    }
    public void setNoOfTeams(int noOfTeams) {
        this.noOfTeams = noOfTeams;
    }
    public String getTournamentStartDate() {
        return tournamentStartDate;
    }
    public void setTournamentStartDate(String tournamentStartDate) {
        this.tournamentStartDate = tournamentStartDate;
    }
    public String getTournamentEndDate() {
        return tournamentEndDate;
    }
    public void setTournamentEndDate(String tournamentEndDate) {
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
    public int getNoOfVenues() {
        return noOfVenues;
    }
    public void setNoOfVenues(int noOfVenues) {
        this.noOfVenues = noOfVenues;
    }
    public int getNoOfUmpires() {
        return noOfUmpires;
    }
    public void setNoOfUmpires(int noOfUmpires) {
        this.noOfUmpires = noOfUmpires;
    }
    private int noOfTeams;
    private String tournamentStartDate;
    private String tournamentEndDate;
    private int tournamentDuration;
    private String nameOfTournament;
    public String getTypeOfTournament() {
        return typeOfTournament;
    }

    public void setTypeOfTournament(String typeOfTournament) {
        this.typeOfTournament = typeOfTournament;
    }

    private String typeOfTournament;
    private int noOfVenues;
    private int noOfUmpires;

    public List<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(List<String> teamNames) {
        this.teamNames = teamNames;
    }

    public List<String> getVenueList() {
        return venueList;
    }

    public void setVenueList(List<String> venueList) {
        this.venueList = venueList;
    }

    public List<String> getUmpireList() {
        return umpireList;
    }

    public void setUmpireList(List<String> umpireList) {
        this.umpireList = umpireList;
    }

    private List<String> teamNames;
    private List<String> venueList;
    private List<String> umpireList;

}