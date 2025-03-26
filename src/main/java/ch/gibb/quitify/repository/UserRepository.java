package ch.gibb.quitify.repository;

import java.util.Optional;

import ch.gibb.quitify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrDisplayName(String username, String displayName);
}
