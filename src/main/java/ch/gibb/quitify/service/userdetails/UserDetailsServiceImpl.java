package ch.gibb.quitify.service.userdetails;

import ch.gibb.quitify.entity.MyUserDetails;
import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.exception.BadRequestException;
import ch.gibb.quitify.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, ch.gibb.quitify.service.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws BadRequestException {
        final User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User with the username " + username + " was not found."));

        return new MyUserDetails(user);
    }
}
