package SocMedApp.backend.model;

import jakarta.persistence.*;

@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String content;

    @OneToOne(cascade = CascadeType.ALL)  // One-to-one relationship with UserImage
    @JoinColumn(name = "post_image_id")  // Foreign key to UserImage
    private PostImage postImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostImage getPostImage() {
        return postImage;
    }

    public void setPostImage(PostImage postImage) {
        this.postImage = postImage;
    }
}
