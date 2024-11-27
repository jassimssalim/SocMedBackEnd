package SocMedApp.backend.services;

import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.model.User;
import SocMedApp.backend.model.UserImage;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    public Map<String, Object> register(User user, MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate file is not empty
            if (file.isEmpty()) {
                response.put("error", "file not existing");
                return response;
            }

            // Extract file information
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes(); // Extract bytes of the file

            // Create the UserImage entity
            UserImage userImage = new UserImage();
            userImage.setFileName(fileName); // Save the file name
            userImage.setFileData(fileBytes); // Save the file bytes

            // Create the User entity
            user.setPassword(bcrypt.encode(user.getPassword()));
            user.setRegisteredDate(LocalDate.now());
            user.setUserImage(userImage); // Associate the image with the user

            // Save the User entity (UserImage is cascaded automatically due to CascadeType.ALL)
            userRepo.save(user);

            response.put("username", user.getUsername());
            response.put("registeredDate", user.getRegisteredDate());

            return response;

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return response;
        }
    }

    public Map<String, Object> verify(User user) {
        String accessToken = "";

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            accessToken = jwtService.generateToken(user.getUsername());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return response;
    }

    //getuserby profile username
    public Map<String, Object> getProfileByUserName(String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepo.findByUsername(username);

        if (user == null) {
            response.put("error", "No user found");
            return response;
        }

        // Get the UserImage and include its details in the response
        UserImage userImage = user.getUserImage();
        Map<String, Object> imageDetails = new HashMap<>();
        imageDetails.put("fileName", (userImage == null) ? "" : userImage.getFileName());
        imageDetails.put("fileData", (userImage == null) ? "" : Base64.getEncoder().encodeToString(userImage.getFileData()));

        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("registeredDate", user.getRegisteredDate());
        response.put("image", imageDetails);
        response.put("graduateSchool", user.getGraduateSchool());
        response.put("age", user.getAge());
        response.put("sex", user.getSex());
        response.put("links", user.getLinks());
        response.put("address", user.getAddress());
        response.put("bio", user.getBio());

        return response;
    }

    //update profile
    public Map<String, Object> updateProfile(User updatedUser, String username) {
        Map<String, Object> response = new HashMap<>();

        // Find the user by username
        User existingUser = userRepo.findByUsername(username);

        if (existingUser == null) {
            response.put("error", "No user found");
            return response;
        }

        // Update the user's fields
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setGraduateSchool(updatedUser.getGraduateSchool());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setSex(updatedUser.getSex());
        existingUser.setLinks(updatedUser.getLinks());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setBio(updatedUser.getBio());

        // Save the updated user
        userRepo.save(existingUser);

        response.put("message", "Profile updated successfully");
        return response;
    }


    public List<Map<String, Object>> searchProfilesByUsernameOrName(String searchParams) {
        List<User> users = userRepo.findAllProfileByUserNameOrName(("%" + searchParams + "%"));

        List<Map<String, Object>> response = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> toAdd = new HashMap<>();
            toAdd.put("username", user.getUsername());
            toAdd.put("name", user.getName());
            toAdd.put("email", user.getEmail());
            response.add(toAdd);
        }

        return response;
    }

    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
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
            userRepo.save(user); // Save the updated user

            return "Password reset successfully";
        } else {
            return "User not found";
        }
    }
}
