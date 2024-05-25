package com.bitscoder.onlinebookstore.repository;

import com.bitscoder.onlinebookstore.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
