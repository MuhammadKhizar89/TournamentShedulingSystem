package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player addPlayerInfo(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public List<Player> getPlayersInfo(int teamId) {
        return playerRepository.findByTeamId(teamId);
    }
}