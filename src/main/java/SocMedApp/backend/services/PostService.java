package SocMedApp.backend.services;

import SocMedApp.backend.model.Comments;
import SocMedApp.backend.model.PostImage;
import SocMedApp.backend.model.Posts;
import SocMedApp.backend.model.User;
import SocMedApp.backend.repo.CommentsRepo;
import SocMedApp.backend.repo.PostRepo;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CommentsRepo commentsRepo;


    public ResponseEntity<String> addPost(Posts post, MultipartFile file) {

        try {

            if (file != null){
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();

            PostImage postImage = new PostImage();
            postImage.setFileName(fileName);
            postImage.setFileData(fileBytes);

            post.setPostImage(postImage);

            }

            postRepo.save(post);

            return ResponseEntity.ok("Post successfully saved");
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public List<Posts> getAllPosts() {
        return postRepo.findAllPosts();
    }


    public List<Posts> getAllPostByUsername(String username) {

        User user = userRepo.findByUsername(username);
        return postRepo.findAllPostsByUserId(user.getId());
    }

    public Posts getPostById(Long id) {
        Optional <Posts> optionalPost = postRepo.findPostById(id);
        Posts post = new Posts();

        if (optionalPost.isPresent()){
            post = optionalPost.get();
        }
        return post;
    }

    public ResponseEntity<String> deletePostById(Long id) {

        try{
            Optional <Posts> optionalPost = postRepo.findPostById(id);
            if (optionalPost.isPresent()){
                Posts post = optionalPost.get();
                postRepo.delete(post);
                return ResponseEntity.ok("Post Successfully deleted");
            } return ResponseEntity.status(500).body("Cannot find post");

        } catch (Exception e){
            return ResponseEntity.status(500).body("Error in deleting the post");
        }

    }

    public List<Comments> getCommentsByPostId(Long postId) {
        return commentsRepo.findCommentsByPostId(postId);
    }

    public ResponseEntity<String> addCommentToPost(Comments newComment) {

        try {
            commentsRepo.save(newComment);
            return ResponseEntity.ok("Successfully added comments");
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error in saving comment");
        }
    }

    public ResponseEntity<String> deleteComment(Long commentId) {
        try {
            Optional<Comments> comment = commentsRepo.findCommentById(commentId);

            if (comment.isPresent()){
                Comments commentToBeDeleted = comment.get();
                commentsRepo.delete(commentToBeDeleted);
                return ResponseEntity.ok("Comment succesfully deleted");
            } else return ResponseEntity.status(500).body("Comment cannot be found");
        } catch (Exception e){
            return ResponseEntity.status(500).body("Error in deleting comments");
        }
    }
}
