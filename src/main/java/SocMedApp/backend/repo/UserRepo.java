package SocMedApp.backend.repo;

import SocMedApp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);  // This method will find the user by email

    User findByUsername(String username);



    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


    boolean existsByEmailAndUsername(String email, String username);


    @Query("select a from User a where a.name  LIKE :searchParam or a.username like :searchParam")
    List<User> findAllProfileByUserNameOrName(String searchParam);

}
