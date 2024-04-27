package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.PlayerOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Player;

import java.util.List;

public interface PlayerService {
    Player addPlayerInfo(Player player);
    List<Player> getPlayersInfo(int teamId);
}
