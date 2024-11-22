package SocMedApp.backend.services;

import SocMedApp.backend.model.Likes;
import SocMedApp.backend.repo.LikesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikesRepo likesRepo;

    public ResponseEntity<String> likeOrUnlikedPost(Long userId, Long commentId, Long postId) {

        if (postId != null) {
            Optional<Likes> optionalLikesInPost = likesRepo.findPostLikeByUserId(userId, postId);
            if (optionalLikesInPost.isPresent()) {
                Likes likes = optionalLikesInPost.get();
                likes.setLiked(!likes.getLiked());
                likesRepo.save(likes);
                return ResponseEntity.ok("Like in post is successfully change.");
            } else {

                Likes likes = new Likes();
                likes.setUserId(userId);
                likes.setPostId(postId);
                likes.setLiked(true);
                likesRepo.save(likes);
                return ResponseEntity.ok("Like successfully added to post");
            }
        }
        if (commentId != null) {
            Optional<Likes> optionalLikesInComment = likesRepo.findCommentLikeByUserId(userId, commentId);
            if (optionalLikesInComment.isPresent()) {

                Likes likes = optionalLikesInComment.get();
                likes.setLiked(!likes.getLiked());
                likesRepo.save(likes);

                return ResponseEntity.ok("Like in comment is successfully change.");
            } else {

                Likes likes = new Likes();
                likes.setUserId(userId);
                likes.setCommentId(commentId);
                likes.setLiked(true);
                likesRepo.save(likes);
                return ResponseEntity.ok("Like successfully added to comment");
            }
        }
        return ResponseEntity.status(500).body("Error in adding likes");
    }

    public List<Likes> getLikes(Long commentId, Long postId) {

        List<Likes> likes = new ArrayList<>();

        if (commentId != null){
            return likesRepo.findAllLikesByCommentId(commentId);
        } else if (postId != null ){
            return likesRepo.findAllLikesByPostId(postId);
        } return likes;
    }
}
