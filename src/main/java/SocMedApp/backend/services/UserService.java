package SocMedApp.backend.services;

import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.dto.ResetPasswordDTOv2;

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
                response.put("error", "File not provided");
                return response;
            }



            // Extract file information
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes(); // Extract bytes of the file

            // Create the UserImage entity
            UserImage userImage = new UserImage();
            userImage.setFileName(fileName); // Save the file name
            userImage.setFileData(fileBytes); // Save the file bytes
            user.setActive(true);

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
        User thisUser = new User();

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()) {


            accessToken = jwtService.generateToken(user.getUsername());
            thisUser = userRepo.findByUsername(user.getUsername());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("userId", thisUser.getId());
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
        response.put("phone", user.getPhone());


        return response;
    }

    //update profile

    public Map<String, Object> updateProfile(User updatedUser, String username) {
        Map<String, Object> response = new HashMap<>();

        // Find the user by username
        User existingUser = userRepo.findByUsername(username);

        if (existingUser == null) {
            response.put("error", "User not found");
            return response;
        }

        // Update fields regardless of null or not
        existingUser.setName(updatedUser.getName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setGraduateSchool(updatedUser.getGraduateSchool());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setSex(updatedUser.getSex());
        existingUser.setLinks(updatedUser.getLinks());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setPhone(updatedUser.getPhone());


        try {
            // Save the updated user
            userRepo.save(existingUser);
            response.put("message", "Profile updated successfully");
            response.put("user", existingUser); // Optional: include updated user details
        } catch (Exception e) {
            response.put("error", "Failed to update profile: " + e.getMessage());
        }

        return response;
    }
    //end


    public List<Map<String, Object>> searchProfilesByUsernameOrName(String searchParams) {
        List<User> users = userRepo.findAllProfileByUserNameOrName(("%" + searchParams + "%"));

        List<Map<String, Object>> response = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> toAdd = new HashMap<>();
            toAdd.put("userName", user.getUsername());
            toAdd.put("name", user.getName());
            toAdd.put("email", user.getEmail());

            // Get the UserImage and include its details in the response
            UserImage userImage = user.getUserImage();
            Map<String, String> imageDetails = new HashMap<>();
            imageDetails.put("fileName", (userImage == null) ? "" : userImage.getFileName());
            imageDetails.put("fileData", (userImage == null) ? "" : Base64.getEncoder().encodeToString(userImage.getFileData()));
            toAdd.put("image", imageDetails);

            response.add(toAdd);
        }

        return response;
    }
//reset password v1 start
    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOptional = userRepo.findByEmail(resetPasswordDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Validate the username
            if (!user.getUsername().equals(resetPasswordDTO.getUsername())) {
                return "Username does not match our records";
            }

            // Create a BCryptPasswordEncoder instance with the same strength (12)
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

            // Check if the old password is correct using bcrypt
            if (!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), user.getPassword())) {
                return "Old password is incorrect";
            }

            // Check if new password and confirm password match
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                return "New password and confirmation do not match";
            }

            // Check if the new password is the same as the old password
            if (passwordEncoder.matches(resetPasswordDTO.getNewPassword(), user.getPassword())) {
                return "New password cannot be the same as the old password";
            }

            // Encrypt the new password before saving
            String encryptedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
            user.setPassword(encryptedPassword);

            // Save the updated user
            user.setActive(true);

            userRepo.save(user);
            user.setActive(true);


            return "Password reset successfully";
        } else {
            return "User not found";
        }
    }
//reset password v1 end
// reset password v2 start

    public String updatePassword(ResetPasswordDTOv2 ResetPasswordDTOv2) {
        User user = userRepo.findByUsername(ResetPasswordDTOv2.getUsername());
        if (user == null) {
            return "User not found";
        }
        // Check if new password and confirm password match
        if (!ResetPasswordDTOv2.getNewPassword().equals(ResetPasswordDTOv2.getConfirmPassword())) {
            return "New password and confirmation do not match";
        }
        // Create a BCryptPasswordEncoder instance with the same strength (12)
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        // Check if the new password is the same as the current password
        if (passwordEncoder.matches(ResetPasswordDTOv2.getNewPassword(), user.getPassword())) {
            return "New password cannot be the same as the current password";
        }
        // Encrypt the new password before saving
        String encryptedPassword = passwordEncoder.encode(ResetPasswordDTOv2.getNewPassword());
        user.setPassword(encryptedPassword);
        // Save the updated user
        userRepo.save(user);

        return "Password updated successfully";
    }

    public Map<String, Object> getUserDetailsByUserId(Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOptional = userRepo.findById(id);
        User user = new User();

        if (!userOptional.isPresent()) {
            response.put("error", "No user found");
            return response;
        } else user = userOptional.get();

        // Get the UserImage and include its details in the response
        UserImage userImage = user.getUserImage();
        Map<String, Object> imageDetails = new HashMap<>();
        imageDetails.put("fileName", (userImage == null) ? "" : userImage.getFileName());
        imageDetails.put("fileData", (userImage == null) ? "" : Base64.getEncoder().encodeToString(userImage.getFileData()));

        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("image", imageDetails);
        return response;
    }

    //delete user

    public String deleteUserByUsername(String username) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            return "User not found";
        }

        userRepo.delete(user);  // This is the delete method inherited from JpaRepository
        return "User deleted successfully";
    }


    //get all users

    public List<Map<String, Object>> getAllUsersExceptCurrent(String currentUsername) {
        List<User> users = userRepo.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (User user : users) {
            if (!user.getUsername().equals(currentUsername)) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("name", user.getName());
                userMap.put("userName", user.getUsername());

                // Get the UserImage and include its details in the response
                UserImage userImage = user.getUserImage();
                Map<String, String> imageDetails = new HashMap<>();
                imageDetails.put("fileName", (userImage == null) ? "" : userImage.getFileName());
                imageDetails.put("fileData", (userImage == null) ? "" : Base64.getEncoder().encodeToString(userImage.getFileData()));

                userMap.put("image", imageDetails);
                response.add(userMap);
            }
        }

        return response;
    }

    //deactivate user
    public String deactivateUserByUsername(String username) {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            return "User not found";
        }

        user.setActive(false);
        userRepo.save(user);  // Save the updated user object

        return "User deactivated successfully";
    }



}
