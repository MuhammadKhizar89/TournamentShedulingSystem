package com.TournamentShedulingSystem.TournamentManagement.ScheduleOtherFiles;

import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Match;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.MatchOtherFiles.MatchRepository;
import com.TournamentShedulingSystem.TournamentManagement.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private MatchRepository matchRepository;
    @Override
    public List<Match> getAllMatchesByTournamentId(int tournamentid) {
        return matchRepository.findAllByTournamentid(tournamentid);
    }
    @Override
    public void populateAllMatchesByTournamentid(Schedule schedule, int tournamentid) {
        List<Match> allMatches = matchRepository.findAllByTournamentid(tournamentid);
        schedule.setListOfTotalMatches(allMatches);
        schedule.setTotalMatches(allMatches.size());
    }
}
