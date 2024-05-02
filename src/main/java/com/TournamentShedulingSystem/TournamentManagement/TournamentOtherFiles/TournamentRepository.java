package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    Optional<Tournament> findById(int id);

    @Query(value = "SELECT MONTH(t.tournamentStartDate) AS month, COUNT(t.id) AS count " +
            "FROM Tournament t " +
            "GROUP BY MONTH(t.tournamentStartDate) " +
            "ORDER BY month")
    List<Object[]> getTournamentsPerMonth();
}

