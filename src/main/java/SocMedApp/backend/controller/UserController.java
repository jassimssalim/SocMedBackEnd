package SocMedApp.backend.controller;

import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.model.User;
import SocMedApp.backend.repo.UserRepo;
import SocMedApp.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;


//    @CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint

    // Register user
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User newUser) {
        return ResponseEntity.ok(userService.register(newUser));
    }

    // authentication/login user
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authLogin(@RequestBody User user) {
        return ResponseEntity.ok(userService.verify(user));
    }

    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        // Retrieve the user by email
        return "";
//        User userOptional = userRepo.findByEmail(resetPasswordDTO.getEmail());
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//
//            // Validate the username
//            if (!user.getUsername().equals(resetPasswordDTO.getUsername())) {
//                return "Username does not match our records";
//            }
//
//            // Check if the old password is correct
//            if (!resetPasswordDTO.getOldPassword().equals(user.getPassword())) {
//                return "Old password is incorrect";
//            }
//
//            // Check if new password and confirm password match
//            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
//                return "New password and confirmation do not match";
//            }
//
//            // Check if the new password is the same as the old password
//            if (resetPasswordDTO.getOldPassword().equals(resetPasswordDTO.getNewPassword())) {
//                return "New password cannot be the same as the old password";
//            }
//
//            // Update the password
//            user.setPassword(resetPasswordDTO.getNewPassword());
//            userRepo.save(user);  // Save the updated user
//
//            return "Password reset successfully";
//        } else {
//            return "User not found";
//        }
    }

    
}
