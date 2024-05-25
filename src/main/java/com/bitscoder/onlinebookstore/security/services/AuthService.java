package com.bitscoder.onlinebookstore.security.services;

import com.bitscoder.onlinebookstore.constant.Roles;
import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.SignInRequest;
import com.bitscoder.onlinebookstore.dto.UserRegistrationRequest;
import com.bitscoder.onlinebookstore.models.User;
import com.bitscoder.onlinebookstore.repository.RoleRepository;
import com.bitscoder.onlinebookstore.repository.UserRepository;
import com.bitscoder.onlinebookstore.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The AuthService class provides authentication services for the application.
 * It handles user registration, sign in, and logout operations.
 *
 * The register method is used to register a new user. It checks if the username and email are already in use,
 * and if the password and confirm password fields match. If the user's role is USER, it saves the user to the repository.
 *
 * The signIn method authenticates a user with their email and password. If the authentication is successful,
 * it generates a JWT token for the user and returns a response with the user's details and the token.
 *
 * The logout method logs out a user by clearing the security context.
 *
 * The createSuccessResponse method is a utility method that creates a success response with a given message and data.
 */

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;

    @Transactional
    public UserRegistrationRequest.Response register(UserRegistrationRequest request) {

        log.info("Register method called with request: {}", request);

        String username = request.getName();
        String email = request.getEmail();

        boolean usernameExists = userRepository.existsByName(username);
        boolean emailExists = userRepository.existsByEmail(email);

        if (usernameExists) {
            log.info("Username already taken: {}", username);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username is already taken!"
            );
        }

        if (emailExists) {
            log.info("Email already in use: {}", email);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email Address already in use!"
            );
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Password and Confirm Password do not match!"
            );
        }

        User user;
        if (request.getRole() == Roles.USER) {
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(request.getRole());
            userRepository.save(user);
        }  else {
            throw new IllegalArgumentException("Invalid user type");
        }

        log.info("User registered successfully with username: {}", username);

        UserRegistrationRequest.Response response = new UserRegistrationRequest.Response(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "User registered successfully",
                request.getName()
        );

        return response;
    }

    public ResponseEntity<ApiResponse<SignInRequest.Response>> signIn(SignInRequest request) {
        log.info("SignIn method called with email: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            log.info("User signed in successfully with email: {}", request.getEmail());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Set<Roles> roles = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> Roles.valueOf(grantedAuthority.getAuthority()))
                    .collect(Collectors.toSet());

            SignInRequest.Response response = new SignInRequest.Response(
                    userDetails.getId(),
                    jwt,
                    "Bearer",
                    userDetails.getUsername(),
                    roles,
                    jwtUtils.getJwtExpirationDate()
            );

            return createSuccessResponse("Login successful",response);
        } catch (BadCredentialsException e) {
            log.info("Invalid email or password for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }


    // HELPER METHOD
    public <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                message,
                data
        ));
    }
}