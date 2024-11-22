package SocMedApp.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String name;
    private String password;
    private String email;
    private LocalDate registeredDate;

    @OneToOne(cascade = CascadeType.ALL)  // One-to-one relationship with UserImage
    @JoinColumn(name = "user_image_id")  // Foreign key to UserImage
    private UserImage userImage;

    @OneToOne(cascade = CascadeType.ALL)  // One-to-one relationship with UserImage
    @JoinColumn(name = "user_image_id")  // Foreign key to UserImage
    private UserImage userImage;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserImage getUserImage() {
        return userImage;
    }

    public void setUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

}
