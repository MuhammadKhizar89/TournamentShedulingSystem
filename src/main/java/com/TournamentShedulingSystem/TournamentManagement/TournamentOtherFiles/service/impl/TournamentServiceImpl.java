package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.service.impl;

import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.repository.TournamentRepository;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {

    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament OrganizeTournament(Tournament tour) {
        return tournamentRepository.save(tour);
    }
}