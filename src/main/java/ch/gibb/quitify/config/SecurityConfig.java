package ch.gibb.quitify.config;

import java.util.Arrays;
import java.util.Optional;

import ch.gibb.quitify.entity.Token;
import ch.gibb.quitify.filter.JwtAuthenticationFilter;
import ch.gibb.quitify.repository.TokenRepository;
import ch.gibb.quitify.service.userdetails.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthenticationFilter authenticationFilter;
    private final TokenRepository tokenRepository;

    public SecurityConfig(
            UserDetailsServiceImpl userDetailsServiceImpl,
            JwtAuthenticationFilter authenticationFilter,
            TokenRepository tokenRepository) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.authenticationFilter = authenticationFilter;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.ACCEPT)
                .maxAge(86400)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.OPTIONS.name())
                .allowCredentials(true)
                .allowedOrigins("http://localhost:5173");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                                // Open
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/api/auth/sign-in/**", "/api/auth/sign-up/**").permitAll()
                                .requestMatchers("/api/auth/me").permitAll()

                                // Auth
                                //.requestMatchers(HttpMethod.GET, "/api/auth/me").hasAuthority(RoleEnum.USER.name())

                                // Any other
                                .anyRequest()
                                .authenticated()
                )
                .userDetailsService(userDetailsServiceImpl)
                .exceptionHandling(
                        e -> e.accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(403))
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(l -> l.logoutUrl("/api/auth/logout")
                        .addLogoutHandler(((request, response, authentication) -> {
                            Optional<String> token = Arrays.stream(request.getCookies())
                                    .filter(cookie -> "jwt".equals(cookie.getName()))
                                    .map(Cookie::getValue)
                                    .findAny();

                            if (token.isEmpty()) {
                                throw new RuntimeException("Token is empty.");
                            }

                            final Token storedToken = this.tokenRepository.findByToken(token.get()).orElse(null);

                            if (storedToken != null) {
                                storedToken.setLoggedOut(true);
                                this.tokenRepository.save(storedToken);
                            }

                            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                            response.setHeader("Access-Control-Allow-Credentials", "true");
                        }))
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
