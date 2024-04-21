package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.service;

import com.TournamentShedulingSystem.TournamentManagement.Tournament;

import java.util.List;

public interface TournamentService {
    List<Tournament> findAllTournaments();
    Tournament OrganizeTournament(Tournament tour);

}

