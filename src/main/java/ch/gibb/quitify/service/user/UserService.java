package ch.gibb.quitify.service.user;

import ch.gibb.quitify.dto.UserDto;
import jakarta.servlet.http.Cookie;

public interface UserService {

    UserDto getCurrentUser();

    UserDto updateUser(UserDto userDto);

    Cookie deleteUser();
}
