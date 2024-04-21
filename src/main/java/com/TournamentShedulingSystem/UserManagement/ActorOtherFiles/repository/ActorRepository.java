package com.TournamentShedulingSystem.UserManagement.ActorOtherFiles.repository;
import com.TournamentShedulingSystem.UserManagement.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    boolean existsByUsernameAndPassword(String username, String password);
    Optional<Actor> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Actor> findByUsernameAndPassword(String username, String password); // New method for retrieving ID by username and password

}
