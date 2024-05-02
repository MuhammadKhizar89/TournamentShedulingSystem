package com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.Tournament;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentRepository;
import com.TournamentShedulingSystem.TournamentManagement.TournamentOtherFiles.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.time.Month;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {
    private TournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }
    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }
    @Override
    public Tournament OrganizeTournament(Tournament tour) {
        return tournamentRepository.save(tour);
    }
    @Override
    public Optional<Tournament> getTournamentInfo(int id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public Map<String,Integer> getTournamentsPerMonth()
    {
        List<Object[]> results=tournamentRepository.getTournamentsPerMonth();

        Map<String,Integer> tournamentsPerMonth=new LinkedHashMap<>();

        for(Object[] result:results)
        {
            int month = (int) result[0];
            int count = ((Number) result[1]).intValue();
            tournamentsPerMonth.put(getMonthName(month), count);
        }

        return tournamentsPerMonth;
    }

    private String getMonthName(int month)
    {
        return Month.of(month).toString();
    }
}
