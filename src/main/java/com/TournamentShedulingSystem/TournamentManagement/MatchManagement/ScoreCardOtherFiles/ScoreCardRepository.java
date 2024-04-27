package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCardOtherFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCard;
import org.springframework.stereotype.Repository;
@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Integer> {
    ScoreCard findByMatchID(int matchId);
}