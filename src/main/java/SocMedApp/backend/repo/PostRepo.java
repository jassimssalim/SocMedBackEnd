package SocMedApp.backend.repo;

import SocMedApp.backend.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepo extends JpaRepository<Posts, Long> {
    @Query("select a from Posts a where a.userId = :userId ORDER BY id DESC")
    List<Posts> findAllPostsByUserId(Long userId);

    @Query("select a from Posts a ORDER BY id DESC")
    List<Posts> findAllPosts();

    Optional<Posts> findPostById(Long id);

    @Transactional
    @Modifying
    @Query("DELETE from Posts where userId = :userId")
    void deleteAllPostsByUserId(Long userId);
}
