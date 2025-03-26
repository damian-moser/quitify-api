package ch.gibb.quitify.controller;

import ch.gibb.quitify.dto.UserDto;
import ch.gibb.quitify.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(this.userService.getCurrentUser());
    }

    @PatchMapping
    public ResponseEntity<?> updateCurrentUserInformation(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(this.userService.updateUser(userDto));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCurrentUser(HttpServletResponse response) {
        response.addCookie(this.userService.deleteUser());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
