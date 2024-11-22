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
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint
@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;


    @CrossOrigin(origins = "http://localhost:3000") // Enable CORS for this specific endpoint

    // Register user
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registerUser(@RequestParam("username") String username,
                                                            @RequestParam("name") String name,
                                                            @RequestParam("email") String email,
                                                            @RequestParam("password") String password,
                                                            @RequestParam("file") MultipartFile file) {
        User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
        return ResponseEntity.ok(userService.register(newUser, file));
    }

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
