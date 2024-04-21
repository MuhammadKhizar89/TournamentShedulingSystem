package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    // Define any custom methods for Player repository if needed
}
