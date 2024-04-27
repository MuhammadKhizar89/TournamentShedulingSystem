package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;

import java.util.List;

public interface MatchService {
    Match getMatchInfo(int id);
    List<Match> saveAllMatches(List<Match> matches);
    List<Match> getAllMatchesByTournamentId(int tournamentId);
}