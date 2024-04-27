package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCardOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.ScoreCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ScoreCardServiceImpl implements ScoreCardService {
    private final ScoreCardRepository scoreCardRepository;
    @Autowired
    public ScoreCardServiceImpl(ScoreCardRepository scoreCardRepository) {
        this.scoreCardRepository = scoreCardRepository;
    }
    @Override
    public ScoreCard getScoreCardInfo(int matchId) {
        return scoreCardRepository.findByMatchID(matchId);
    }
}
