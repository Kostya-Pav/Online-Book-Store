package com.onlinebookstore.repository.user;

import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
