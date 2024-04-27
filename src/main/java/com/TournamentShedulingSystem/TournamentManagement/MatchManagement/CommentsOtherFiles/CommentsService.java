package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;

import java.util.List;
public interface CommentsService {
    List<Comments> getComments(int matchId);
    void addComment(Comments comment);
}