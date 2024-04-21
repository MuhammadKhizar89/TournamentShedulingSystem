package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.repository;

import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    Optional<Tournament> findById(int id);

}

