package ch.gibb.quitify.service.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${application.jwt.secret}")
    private String SECRET_KEY;

    private final TokenRepository tokenRepository;

    public JwtServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    private SecretKey getSecretKey() {
        final byte[] keyBytes = Decoders.BASE64URL.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public Boolean isValid(String token, UserDetails user) {
        final String username = extractUsername(token);

        final Boolean isValidToken = this.tokenRepository.findByToken(token)
                .map(t -> !t.getLoggedOut()).orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && isValidToken;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
