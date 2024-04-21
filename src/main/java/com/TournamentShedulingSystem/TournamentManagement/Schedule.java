package com.TournamentShedulingSystem.TournamentManagement;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;

import java.util.List;

public class Schedule {
        private List<Match> listOfTotalMatches;
        private int totalMatches;

        // Getters and setters
        public List<Match> getListOfTotalMatches() {
                return listOfTotalMatches;
        }

        public void setListOfTotalMatches(List<Match> listOfTotalMatches) {
                this.listOfTotalMatches = listOfTotalMatches;
        }

        public int getTotalMatches() {
                return totalMatches;
        }

        public void setTotalMatches(int totalMatches) {
                this.totalMatches = totalMatches;
        }
}
