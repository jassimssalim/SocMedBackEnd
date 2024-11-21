package SocMedApp.backend.repo;

import SocMedApp.backend.model.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepo extends JpaRepository<UserImage, Long> {
}
