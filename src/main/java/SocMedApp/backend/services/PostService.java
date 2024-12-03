package SocMedApp.backend.services;

import SocMedApp.backend.model.*;
import SocMedApp.backend.repo.CommentsRepo;
import SocMedApp.backend.repo.PostRepo;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

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

            if (file != null) {
                String fileName = file.getOriginalFilename();
                byte[] fileBytes = file.getBytes();

                PostImage postImage = new PostImage();
                postImage.setFileName(fileName);
                postImage.setFileData(fileBytes);

                post.setPostImage(postImage);

            }
            post.setPostedDate(LocalDate.now());

            postRepo.save(post);

            return ResponseEntity.ok("Post successfully saved");
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    public List<Object> getAllPosts() {
        List<Posts> postsList = postRepo.findAllPosts();
        List<Object> updatedList = new ArrayList<>();


        for(Posts post : postsList){

            Map<String, Object> toAdd = new HashMap<>();

            PostImage postImage = post.getPostImage();
            Map<String, Object> imageDetails = new HashMap<>();

            imageDetails.put("fileName", (postImage == null) ? "" : postImage.getFileName());
            imageDetails.put("fileData", (postImage == null) ? "" : ((postImage.getFileData() == null)? "" : Base64.getEncoder().encodeToString(postImage.getFileData())));

            toAdd.put("id", post.getId());
            toAdd.put("userId", post.getUserId());
            toAdd.put("postImage", imageDetails);
            toAdd.put("datePosted", post.getPostedDate());
            toAdd.put("content", post.getContent());
            updatedList.add(toAdd);
        }

        return updatedList;
    }


    public List<Object> getAllPostByUsername(String username) {

        User user = userRepo.findByUsername(username);

        List<Posts> postsList = postRepo.findAllPostsByUserId(user.getId());
        List<Object> updatedList = new ArrayList<>();

        for(Posts post : postsList){

            Map<String, Object> toAdd = new HashMap<>();

            PostImage postImage = post.getPostImage();
            Map<String, Object> imageDetails = new HashMap<>();

            imageDetails.put("fileName", (postImage == null) ? "" : postImage.getFileName());
            imageDetails.put("fileData", (postImage == null) ? "" : ((postImage.getFileData() == null)? "" : Base64.getEncoder().encodeToString(postImage.getFileData())));

            toAdd.put("id", post.getId());
            toAdd.put("userId", post.getUserId());
            toAdd.put("postImage", imageDetails);
            toAdd.put("datePosted", post.getPostedDate());
            toAdd.put("content", post.getContent());
            updatedList.add(toAdd);
        }

        return updatedList;
    }

    public Object getPostById(Long id) {
        Optional <Posts> optionalPost = postRepo.findPostById(id);
        Map<String, Object> response = new HashMap<>();

        if (optionalPost.isPresent()){
            Posts post = optionalPost.get();

                PostImage postImage = post.getPostImage();
                Map<String, Object> imageDetails = new HashMap<>();
                imageDetails.put("fileName", (postImage == null) ? "" : postImage.getFileName());
                imageDetails.put("fileData", (postImage == null) ? "" : ((postImage.getFileData() == null)? "" : Base64.getEncoder().encodeToString(postImage.getFileData())));

                response.put("id", post.getId());
                response.put("userId", post.getUserId());
                response.put("postImage", imageDetails);
                response.put("datePosted", post.getPostedDate());
                response.put("content", post.getContent());
            }

        return response;
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

    public ResponseEntity<Object> editPost(Long postId, Long userId, String content, MultipartFile file, boolean isPhotoDeleted) {

        Map<String, Object> response = new HashMap<>();

        try {

            Optional<Posts> optionalPost = postRepo.findPostById(postId);

            if(optionalPost.isPresent()){

                Posts post = optionalPost.get();
                post.setContent(content);
                post.setUserId(userId);
                PostImage currentPostImage = post.getPostImage();


                if(isPhotoDeleted){
                    currentPostImage.setFileName(null);
                    currentPostImage.setFileData(null);
                    post.setPostImage(currentPostImage);
                }

                if (file != null) {
                    String fileName = file.getOriginalFilename();
                    byte[] fileBytes = file.getBytes();

                    currentPostImage.setFileName(fileName);
                    currentPostImage.setFileData(fileBytes);
                    post.setPostImage(currentPostImage);

                }

                postRepo.save(post);

                Map<String, Object> imageDetails = new HashMap<>();
                imageDetails.put("fileName", (currentPostImage == null) ? "" : currentPostImage.getFileName());
                imageDetails.put("fileData", (currentPostImage == null) ? "" : ((currentPostImage.getFileData() == null)? "" : Base64.getEncoder().encodeToString(currentPostImage.getFileData())));
                response.put("id", post.getId());
                response.put("userId", post.getUserId());
                response.put("postImage", imageDetails);
                response.put("datePosted", post.getPostedDate());
                response.put("content", post.getContent());

                return ResponseEntity.ok(response);
            } else return ResponseEntity.status(500).body("Post not existing.");

        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
