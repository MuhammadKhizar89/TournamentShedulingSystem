package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {
    // You can define custom query methods here if needed
    List<Match> findAllByTournamentid(int tournamentid);

}