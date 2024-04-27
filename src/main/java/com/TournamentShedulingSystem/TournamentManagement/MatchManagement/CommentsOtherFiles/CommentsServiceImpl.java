package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsServiceImpl(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Override
    public List<Comments> getComments(int matchId) {
        return commentsRepository.findByMatchId(matchId);
    }

    @Override
    public void addComment(Comments comment) {
        commentsRepository.save(comment);
    }
}