package com.TournamentShedulingSystem.UserManagement.ActorOtherFiles;
import com.TournamentShedulingSystem.UserManagement.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;

    @Autowired
    public ActorServiceImpl(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Override
    public Actor signin(Actor actor) {
        return actorRepository.save(actor);
    }

    @Override
    public Actor findByUsername(String username) {
        return null;
    }

//    @Override
//    public Actor saveActor(Actor actor) {
//        return null;
//    }

//    @Override
//    public Actor findByUsername(String username) {
//        return actorRepository.findByUsername(username);
//    }


    @Override
    public boolean exists(String username, String password) {
        Optional<Actor> existingActor = actorRepository.findByUsername(username);
        return existingActor.isPresent() && existingActor.get().getPassword().equals(password);
    }
    @Override
    public boolean existsByUsername(String username) {
        return actorRepository.existsByUsername(username);
    }
    @Override
    public Long getIdByUsernameAndPassword(String username, String password) {
        Optional<Actor> existingActor = actorRepository.findByUsernameAndPassword(username, password);
        return existingActor.map(Actor::getId).orElse(null);
    }
    @Override
    public Actor findByid(Long id) {
        return actorRepository.findByid(id);
    }
}
