package ch.gibb.quitify.repository;

import ch.gibb.quitify.entity.Role;
import ch.gibb.quitify.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);
}
