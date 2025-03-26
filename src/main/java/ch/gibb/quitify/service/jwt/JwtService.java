package ch.gibb.quitify.service.jwt;

import java.util.function.Function;

import ch.gibb.quitify.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(User user);

    Boolean isValid(String token, UserDetails user);

    <T> T extractClaim(String token, Function<Claims, T> resolver);

    String extractUsername(String token);
}
