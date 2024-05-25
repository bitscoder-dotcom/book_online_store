package com.bitscoder.onlinebookstore.security.services;

import com.bitscoder.onlinebookstore.models.User;
import com.bitscoder.onlinebookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The UserDetailsServiceImpl class implements the UserDetailsService interface and provides user details services for the application.
 * It has a UserRepository to interact with the user data.
 *
 * The loadUserByUsername method is used to load a user by their email. It fetches the user from the UserRepository.
 * If the user is not found, it throws a UsernameNotFoundException. If the user is found, it logs the event and returns a UserDetailsImpl object built from the user.
 */

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Trying to get user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.info("Found user: {}", email);
        return UserDetailsImpl.build(user);
    }
}
