package SocMedApp.backend.repo;

import SocMedApp.backend.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsRepo extends JpaRepository<Comments, Long> {

    List<Comments> findCommentsByPostId(Long postId);

    Optional<Comments> findCommentById(Long id);

}
