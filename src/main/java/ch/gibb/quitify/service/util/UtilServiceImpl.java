package ch.gibb.quitify.service.util;

import ch.gibb.quitify.entity.Token;
import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.repository.TokenRepository;
import ch.gibb.quitify.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UtilServiceImpl implements UtilService {

    @Override
    public File saveAsTempFile(MultipartFile file) throws IOException {
        final String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        File tempFile = File.createTempFile("ocr-", "-" + originalFilename);
        file.transferTo(tempFile);
        return tempFile;
    }

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private static final int TOKEN_MAX_AGE = 60 * 60 * 24;

    public UtilServiceImpl(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User getSignedInUser() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            final String username = ((UserDetails) principal).getUsername();
            final Optional<User> user = this.userRepository.findByUsername(username);

            if (user.isPresent()) {
                return user.get();
            }
        }

        return null;
    }

    public Cookie createCookie(String token) {
        final Cookie cookie = new Cookie("jwt", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(TOKEN_MAX_AGE);
        return cookie;
    }

    public Cookie createCookie() {
        final Cookie cookie = new Cookie("jwt", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    public void revokeAllTokens(User user) {
        final List<Token> validTokenListUser = this.tokenRepository.findAllTokenByUser(user.getId());

        if (!validTokenListUser.isEmpty()) {
            validTokenListUser.forEach(t -> t.setLoggedOut(true));
        }

        this.tokenRepository.saveAll(validTokenListUser);
    }

    public void deleteAllTokens(Long userId) {
        final List<Token> validTokenListUser = this.tokenRepository.findAllTokenByUser(userId);
        this.tokenRepository.deleteAll(validTokenListUser);
    }

    public void saveToken(String jwt, User user) {
        final Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        this.tokenRepository.save(token);
    }
}
