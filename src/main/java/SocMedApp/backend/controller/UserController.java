package SocMedApp.backend.controller;

import SocMedApp.backend.dto.ResetPasswordDTO;
import SocMedApp.backend.model.User;
import SocMedApp.backend.model.UserImage;
import SocMedApp.backend.repo.UserImageRepo;
import SocMedApp.backend.repo.UserRepo;
import SocMedApp.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(userService.verify(user));
    }

    //get profile by username
    @GetMapping("/profiles/{username}")
    public ResponseEntity<Map<String, Object>> getProfileByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getProfileByUserName(username));
    }

    //update user by username
    @PutMapping("/update/{username}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String username,
            @RequestBody User updatedUser
    ) {
        Map<String, Object> response = userService.updateProfile(updatedUser, username);


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


    // Reset password
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }


}


