package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamNameRepository extends JpaRepository<Tournament.TeamName, Integer> {
    List<Tournament.TeamName> findByTournamentId(int tournamentId);
}