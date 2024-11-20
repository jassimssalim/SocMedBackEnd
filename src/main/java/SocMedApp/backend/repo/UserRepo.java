package SocMedApp.backend.repo;

import SocMedApp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);  // This method will find the user by email

    boolean existsByEmail(String email);


}
