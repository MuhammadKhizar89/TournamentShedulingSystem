package com.TournamentShedulingSystem.TournamentManagement.MatchManagement.CommentsOtherFiles;
import com.TournamentShedulingSystem.TournamentManagement.MatchManagement.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    List<Comments> findByMatchId(int matchId);
}