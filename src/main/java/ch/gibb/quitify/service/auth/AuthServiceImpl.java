package ch.gibb.quitify.service.auth;

import java.sql.Timestamp;
import java.util.Optional;

import ch.gibb.quitify.dto.SignInDto;
import ch.gibb.quitify.dto.SignUpDto;
import ch.gibb.quitify.entity.Role;
import ch.gibb.quitify.entity.User;
import ch.gibb.quitify.entity.UserRole;
import ch.gibb.quitify.enums.RoleEnum;
import ch.gibb.quitify.exception.BadRequestException;
import ch.gibb.quitify.repository.RoleRepository;
import ch.gibb.quitify.repository.UserRepository;
import ch.gibb.quitify.repository.UserRoleRepository;
import ch.gibb.quitify.service.jwt.JwtService;
import ch.gibb.quitify.service.util.UtilService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UtilService utilService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtServiceImpl,
            AuthenticationManager authenticationManager,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            UtilService utilService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtServiceImpl;
        this.authenticationManager = authenticationManager;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.utilService = utilService;
    }

    @Override
    public Cookie signUp(SignUpDto request) throws BadRequestException {
        if (request.getUsername().isBlank() || request.getPassword().isBlank()) {
            throw new BadRequestException("no provided credentials");
        }

        final Optional<User> existingUser = this.userRepository.findByUsernameOrDisplayName(request.getUsername(), request.getDisplayName());

        if (existingUser.isPresent()) {
            throw new BadRequestException("Versuche es mit einem anderen Benutzernamen.");
        }

        final User user = new User();
        user.setDisplayName(request.getDisplayName());
        user.setUsername(request.getUsername());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setChangePassword(false);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setIsPublic(false);
        this.userRepository.save(user);

        final Role role = this.roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new BadRequestException("Ein Fehler ist aufgetreten."));

        final UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(user);
        this.userRoleRepository.save(userRole);

        final String token = jwtService.generateToken(user);
        this.utilService.saveToken(token, user);

        return this.utilService.createCookie(token);
    }

    @Override
    public Cookie signIn(SignInDto request) throws BadRequestException {
        if (request.getUsername().isBlank() || request.getPassword().isBlank()) {
            throw new BadRequestException("no provided credentials.");
        }

        final User user = this.userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Falsche Anmeldedaten."));

        if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Falsche Anmeldedaten.");
        }

        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        final String token = jwtService.generateToken(user);
        this.utilService.revokeAllTokens(user);
        this.utilService.saveToken(token, user);

        return this.utilService.createCookie(token);
    }
}
