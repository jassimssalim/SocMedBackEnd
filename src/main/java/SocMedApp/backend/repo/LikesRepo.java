package SocMedApp.backend.repo;

import SocMedApp.backend.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesRepo extends JpaRepository<Likes, Long> {

    @Query("select a from Likes a where a.postId = :postId AND a.isLiked = true")
    List<Likes> findAllLikesByPostId(Long postId);

    @Query("select a from Likes a where a.commentId = :commentId AND a.isLiked = true")
    List<Likes> findAllLikesByCommentId(Long commentId);

    @Query("select a from Likes a where a.userId = :userId AND a.commentId = :commentId")
    Optional<Likes> findCommentLikeByUserId(Long userId, Long commentId);

    @Query("select a from Likes a where a.userId = :userId AND a.postId = :postId")
    Optional<Likes> findPostLikeByUserId(Long userId, Long postId);


}
