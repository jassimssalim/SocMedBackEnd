package SocMedApp.backend.controller;

import SocMedApp.backend.model.Likes;
import SocMedApp.backend.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LikesController {

    @Autowired
    private LikeService likeService;

    //add or delete likes in post or comment
    //only one between commentId or postId has a value (other should be null)

    @PostMapping("/likes/add")
    public ResponseEntity<String> likeOrUnlikedPost(@RequestParam("userId") Long userId,
                                                    @RequestParam(value = "commentId", required = false) Long commentId,
                                                    @RequestParam(value = "postId", required = false) Long postId){
        return likeService.likeOrUnlikedPost(userId, commentId, postId);
    }

    @GetMapping("/likes")
    public ResponseEntity<List<Likes>> getLikes(@RequestParam(value = "commentId", required = false) Long commentId,
                                               @RequestParam(value = "postId", required = false) Long postId){
        return ResponseEntity.ok(likeService.getLikes(commentId, postId));
    }
}
