package SocMedApp.backend.repo;

import SocMedApp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    User findByEmail(String email);  // This method will find the user by email

    User findByUsername(String username);
}
