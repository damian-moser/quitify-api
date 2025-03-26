package ch.gibb.quitify.controller;

import ch.gibb.quitify.dto.SignInDto;
import ch.gibb.quitify.dto.SignUpDto;
import ch.gibb.quitify.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto credentials, HttpServletResponse response) {
        response.addCookie(this.authService.signUp(credentials));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDto credentials, HttpServletResponse response) {
        response.addCookie(this.authService.signIn(credentials));
        return ResponseEntity.ok().build();
    }
}
