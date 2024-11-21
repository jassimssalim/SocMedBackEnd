package SocMedApp.backend.controller;

import SocMedApp.backend.dto.LoginDTO;
import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.model.User;
import SocMedApp.backend.model.UserImage;
import SocMedApp.backend.repo.UserImageRepo;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint
@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserImageRepo userImageRepo;

    // Register user with image upload
    @PostMapping("/user")
    public ResponseEntity<String> registerUser(
            @RequestParam("username") String username,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("file") MultipartFile file) {

        try {
            // Validate file is not empty
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body("File is required.");
            }

            // Extract file information
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes(); // Extract bytes of the file

            // Create the UserImage entity
            UserImage userImage = new UserImage();
            userImage.setFileName(fileName); // Save the file name
            userImage.setFileData(fileBytes); // Save the file bytes

            // Create the User entity
            User user = new User();
            user.setUsername(username);
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserImage(userImage); // Associate the image with the user

            // Save the User entity (UserImage is cascaded automatically due to CascadeType.ALL)
            userRepo.save(user);

            return ResponseEntity.ok("User registered successfully!");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }


    // Login user
    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        // Retrieve the user by email
        Optional<User> userOptional = userRepo.findByEmail(loginDTO.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Compare the provided plain-text password with the stored password
            if (loginDTO.getPassword().equals(user.getPassword())) {
                // If the password matches, return success message
                return "Login successful";
            } else {
                return "Invalid password";
            }
        } else {
            return "User not found";
        }
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        // Retrieve the user by email
        Optional<User> userOptional = userRepo.findByEmail(resetPasswordDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Validate the username
            if (!user.getUsername().equals(resetPasswordDTO.getUsername())) {
                return "Username does not match our records";
            }

            // Check if the old password is correct
            if (!resetPasswordDTO.getOldPassword().equals(user.getPassword())) {
                return "Old password is incorrect";
            }

            // Check if new password and confirm password match
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                return "New password and confirmation do not match";
            }

            // Check if the new password is the same as the old password
            if (resetPasswordDTO.getOldPassword().equals(resetPasswordDTO.getNewPassword())) {
                return "New password cannot be the same as the old password";
            }

            // Update the password
            user.setPassword(resetPasswordDTO.getNewPassword());
            userRepo.save(user);  // Save the updated user

            return "Password reset successfully";
        } else {
            return "User not found";
        }
    }
}
