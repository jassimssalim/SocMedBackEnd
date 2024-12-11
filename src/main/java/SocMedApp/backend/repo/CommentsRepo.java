package SocMedApp.backend.repo;

import SocMedApp.backend.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface CommentsRepo extends JpaRepository<Comments, Long> {

    List<Comments> findCommentsByPostId(Long postId);

    Optional<Comments> findCommentById(Long id);

    @Transactional
    @Modifying
    @Query("Delete from Comments where userId = :userId")
    void deleteAllCommentsByUserId(Long userId);

}
