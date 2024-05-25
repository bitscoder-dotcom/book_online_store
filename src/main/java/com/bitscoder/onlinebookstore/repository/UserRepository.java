package com.bitscoder.onlinebookstore.repository;

import com.bitscoder.onlinebookstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *  This interface enables CRUD operations to be performed on User entities identified by a String ID
 */
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String username);
}
