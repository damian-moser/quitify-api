package ch.gibb.quitify.service.auth;

import ch.gibb.quitify.dto.SignInDto;
import ch.gibb.quitify.dto.SignUpDto;
import ch.gibb.quitify.exception.BadRequestException;
import jakarta.servlet.http.Cookie;

public interface AuthService {

    Cookie signUp(SignUpDto credentials) throws BadRequestException;

    Cookie signIn(SignInDto credentials) throws BadRequestException;
}
