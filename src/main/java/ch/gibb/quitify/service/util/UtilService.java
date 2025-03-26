package ch.gibb.quitify.service.util;

import ch.gibb.quitify.entity.User;
import jakarta.servlet.http.Cookie;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface UtilService {

    File saveAsTempFile(MultipartFile file) throws IOException;

    User getSignedInUser();

    Cookie createCookie(String token);

    Cookie createCookie();

    void revokeAllTokens(User user);

    void deleteAllTokens(Long userId);

    void saveToken(String jwt, User user);
}
