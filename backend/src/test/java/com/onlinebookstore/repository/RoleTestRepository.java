package com.onlinebookstore.repository;

import com.onlinebookstore.model.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoleTestRepository extends JpaRepository<Role, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM roles WHERE id = ?1", nativeQuery = true)
    void deleteRoleById(Long id);
}
