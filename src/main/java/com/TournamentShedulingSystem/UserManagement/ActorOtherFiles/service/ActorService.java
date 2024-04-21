package com.TournamentShedulingSystem.UserManagement.ActorOtherFiles.service;

import com.TournamentShedulingSystem.UserManagement.Actor;

public interface ActorService {
    Actor signin(Actor actor);
    Actor findByUsername(String username);
    boolean exists(String username, String password);
    boolean existsByUsername(String username);
    Long getIdByUsernameAndPassword(String username, String password);
}
