package ch.gibb.quitify.repository;

import java.util.List;
import java.util.Optional;

import ch.gibb.quitify.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("select t from Token t inner join User u on t.user.id = u.id where t.user.id = :userId and t.loggedOut = false")
    List<Token> findAllTokenByUser(Long userId);

    Optional<Token> findByToken(String token);
}
