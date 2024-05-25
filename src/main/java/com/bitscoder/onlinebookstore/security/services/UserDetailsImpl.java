package com.bitscoder.onlinebookstore.security.services;

import com.bitscoder.onlinebookstore.constant.Roles;
import com.bitscoder.onlinebookstore.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The UserDetailsImpl class implements the UserDetails interface and provides the details of a user.
 * It includes fields such as id, name, email, password, and authorities.
 *
 * The UserDetailsImpl constructor initializes these fields and logs the creation of the UserDetails object.
 *
 * The build method is a static method that creates a UserDetailsImpl object from a User object.
 * It assigns the user's id, email, and password to the UserDetailsImpl object, and sets the authorities to USER.
 *
 * The class also overrides the methods from the UserDetails interface to get the user's authorities, id, email, password,
 * and username, and to check if the account is non-expired, non-locked, and enabled.
 *
 * The equals method checks if two UserDetailsImpl objects are equal based on their id.
 *
 * The toString method returns a string representation of the UserDetailsImpl object.
 */

@RequiredArgsConstructor
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            String id, String email, String password,
            List<GrantedAuthority> authorities) {
        this.id = id;
        this.name = email;
        this.email = email;
        this.password = password;
        this.authorities = authorities;

        logUserDetailsCreation();
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Roles.USER.name()));

        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
        log.info("UserDetailsImpl built for user: {}", userDetails.getUsername());
        log.debug("UserDetailsImpl: {}", userDetails);

        return userDetails;
    }

    private void logUserDetailsCreation() {
        log.info("UserDetails created for user: {}", name);
        log.debug("UserDetails: {}", this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
