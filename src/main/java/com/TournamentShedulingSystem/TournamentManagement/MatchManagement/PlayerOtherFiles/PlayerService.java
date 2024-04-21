package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;
import java.util.List;
public interface PlayerService {
    List<Player> getAllPlayers();
    Player getPlayerById(int id);
    Player savePlayer(Player player);
    void deletePlayer(int id);
}