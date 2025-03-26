package ch.gibb.quitify.service.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsService {

    UserDetails loadUserByUsername(String username) throws RuntimeException;
}
