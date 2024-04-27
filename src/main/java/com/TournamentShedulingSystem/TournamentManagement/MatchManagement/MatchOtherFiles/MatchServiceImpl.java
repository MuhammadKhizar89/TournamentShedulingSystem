package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {
    @Autowired
    private MatchRepository matchRepository;
    @Override
    public Match getMatchInfo(int id) {
        return matchRepository.findById(id).orElse(null);
    }
    @Override
    public List<Match> saveAllMatches(List<Match> matches) {
        return matchRepository.saveAll(matches);
    }
    @Override
    public List<Match> getAllMatchesByTournamentId(int tournamentId) {
        return matchRepository.findAllByTournamentid(tournamentId);
    }
}
