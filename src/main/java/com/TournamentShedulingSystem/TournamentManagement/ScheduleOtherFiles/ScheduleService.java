package com.TournamentShedulingSystem.TournamentManagement.ScheduleOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import com.TournamentShedulingSystem.TournamentManagement.Schedule;

import java.util.List;

public interface ScheduleService {
    List<Match> getAllMatchesByTournamentId(int tournamentid);
    void populateAllMatchesByTournamentid(Schedule schedule, int tournamentid);
}