package ch.gibb.quitify.service.user;

import ch.gibb.quitify.dto.UserDto;
import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.repository.UserRepository;
import ch.gibb.quitify.service.util.UtilService;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UtilService utilService;
    private final UserRepository userRepository;

    public UserServiceImpl(UtilService utilService, UserRepository userRepository) {
        this.utilService = utilService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getCurrentUser() {
        return new UserDto(this.utilService.getSignedInUser());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final User user = this.utilService.getSignedInUser();

        user.setIsPublic(userDto.isPublic());
        user.setDisplayName(userDto.getDisplayName());

        final User updatedUser = this.userRepository.save(user);
        return new UserDto(updatedUser);
    }

    @Override
    public Cookie deleteUser() {
        final User user = this.utilService.getSignedInUser();
        this.utilService.deleteAllTokens(user.getId());
        this.userRepository.delete(user);

        return this.utilService.createCookie();
    }
}
