package SocMedApp.backend.controller;

import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.dto.ResetPasswordDTOv2;

import SocMedApp.backend.model.User;
import SocMedApp.backend.repo.UserRepo;
import SocMedApp.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.HashMap;
import java.util.Map;


import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint
@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;


    @CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint


   //register
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registerUser(@RequestParam("username") String username,
                                                            @RequestParam("name") String name,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("password") String password,
                                                            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();
        // Validate if both the email and username already exist
        if (userRepo.existsByEmailAndUsername(email, username)) {
            // If both email and username exist
            response.put("bothError", "Username and Email already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // Return 409 Conflict with the bothError
        }

        // Validate if the email already exists
        if (userRepo.existsByEmail(email)) {
            response.put("emailError", "Email already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // Return 409 Conflict with the email error
        }

        // Validate if the username already exists
        if (userRepo.existsByUsername(username)) {
            response.put("usernameError", "Username already taken");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // Return 409 Conflict with the username error
        }

        // If no duplicates, create the user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        // Call the userService to handle registration logic, such as saving to DB
        Map<String, Object> registrationResponse = userService.register(newUser, file);

        // Check if the response contains an error field
        if (registrationResponse.containsKey("error")) {
            return ResponseEntity.badRequest().body(registrationResponse);  // Return 400 if an error occurs
        }

        // Return success response
        return ResponseEntity.ok(registrationResponse);  // Return 200 for successful registration
    }
    //register end

    // authentication/login user
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authLogin(@RequestBody User user) {
        // Fetch the user from the database using the username
        User existingUser = userRepo.findByUsername(user.getUsername());



        if (!existingUser.isActive()) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorInactive", "Your account is inactive.");
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        // If the user is active, proceed with the login verification
        Map<String, Object> response = userService.verify(user);
        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }


    //get profile by username
    @GetMapping("/profiles/{username}")
    public ResponseEntity<Map<String, Object>> getProfileByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getProfileByUserName(username));
    }

    //get profile by userId
    @GetMapping("/profiles/userId/{userId}")
    public ResponseEntity<Map<String, Object>> getProfileByUsername(@PathVariable("userId") Long id) {
        return ResponseEntity.ok(userService.getUserDetailsByUserId(id));
    }

    //update user by username
    @PutMapping("/update/{username}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String username,
            @RequestBody User updatedUser
    ) {
        Map<String, Object> response = new HashMap<>();

        String email = updatedUser.getEmail(); // Extract email from the updatedUser object

        // Check if the email is already in use
        if (userRepo.existsByEmail(email)) {
            // Verify that the email doesn't belong to the user being updated
            User currentUser = userRepo.findByUsername(username);
            if (currentUser != null && !currentUser.getEmail().equals(email)) {
                response.put("emailError", "Email already taken");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // Return 409 Conflict with the email error
            }
        }

        // Delegate the profile update logic to the service
        response = userService.updateProfile(updatedUser, username);

        // Return appropriate response based on the operation's outcome
        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }



    //search profile by username or name
    @GetMapping("/profiles/")
    public ResponseEntity<List<Map<String, Object>>> getProfileByUsernameOrName(@RequestParam(name = "searchParam") String searchParam) {
        return ResponseEntity.ok(userService.searchProfilesByUsernameOrName(searchParam));
    }


    // Reset password V1
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }

    //Update password v2
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody ResetPasswordDTOv2 resetPasswordDTOv2) {
        String result = userService.updatePassword(resetPasswordDTOv2);
        if (result.equals("Password updated successfully")) {
            return ResponseEntity.ok(result); // Return 200 OK with success message
        } else {
            return ResponseEntity.badRequest().body(result); // Return 400 Bad Request with error message
        }
    }

    // Delete user by username
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("username") String username) {
        String result = userService.deleteUserByUsername(username);
        Map<String, Object> response = new HashMap<>();

        if (result.equals("User not found")) {
            response.put("error", result);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // Return 404 if user not found
        }

        response.put("message", result);  // Success message
        return ResponseEntity.ok(response);  // Return 200 for successful deletion
    }

    //GETUSER EXCLUDE
    @GetMapping("/users/excludeCurrent")
    public List<Map<String, Object>> getAllUsersExceptCurrent(@RequestParam String currentUsername) {
        return userService.getAllUsersExceptCurrent(currentUsername);
    }

    //DEACTIVE USER
    // Deactivate user by username
    @PutMapping("/deactivate/{username}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable("username") String username) {
        String result = userService.deactivateUserByUsername(username);
        Map<String, Object> response = new HashMap<>();

        if (result.equals("User not found")) {
            response.put("error", result);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // Return 404 if user not found
        }

        response.put("message", result);  // Success message
        return ResponseEntity.ok(response);  // Return 200 for successful deactivation
    }


}


