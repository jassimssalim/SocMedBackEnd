package SocMedApp.backend.services;

import SocMedApp.backend.model.User;
import SocMedApp.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Map<String, Object>  register(User user){
        user.setPassword(bcrypt.encode(user.getPassword()));
        userRepo.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("registeredDate", new Date(System.currentTimeMillis()));
        return response;
    }

    public Map<String, Object> verify(User user){
        String accessToken ="";

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()){
            accessToken = jwtService.generateToken(user.getUsername());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return response;
    }

    public Map<String, Object> getProfileByUserName(String username) {
        User user = userRepo.findByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        return response;
    }

    public List<Map<String, Object>> searchProfilesByUsernameOrName(String searchParams) {
        List<User> users = userRepo.findAllProfileByUserNameOrName(("%" + searchParams + "%"));

        List<Map<String, Object>> response = new ArrayList<>();

        for(User user: users){
            Map<String, Object> toAdd = new HashMap<>();
            toAdd.put("username", user.getUsername());
            toAdd.put("name", user.getName());
            toAdd.put("email", user.getEmail());
            response.add(toAdd);
        }

        return response;
    }
}
