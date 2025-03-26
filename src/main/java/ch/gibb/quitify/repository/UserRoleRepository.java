package ch.gibb.quitify.repository;

import java.util.Optional;

import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByUser(User user);
}
