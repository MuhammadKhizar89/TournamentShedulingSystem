package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;

import java.util.List;
import java.util.Map;
import java.util.Optional;
public interface TournamentService {
    List<Tournament> findAllTournaments();
    Tournament OrganizeTournament(Tournament tour);
    Optional<Tournament> getTournamentInfo(int id);
    public Map<String,Integer> getTournamentsPerMonth();

}

