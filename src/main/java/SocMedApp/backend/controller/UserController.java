package SocMedApp.backend.controller;

import SocMedApp.backend.dto.LoginDTO;
import SocMedApp.backend.dto.ResetPasswordDTO;

import SocMedApp.backend.model.User;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint

    // Register user
    @PostMapping("/user")
    public User registerUser(@RequestBody User newUser) {
        // Store the password as plain text (This is not secure, remove encryption for now)
        return userRepo.save(newUser);
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
