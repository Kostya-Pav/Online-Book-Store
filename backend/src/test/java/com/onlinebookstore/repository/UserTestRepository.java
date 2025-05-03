package com.onlinebookstore.repository;

import com.onlinebookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserTestRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_roles WHERE user_id = ?1", nativeQuery = true)
    void deleteUserRolesByUserId(Long userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users WHERE id = ?1", nativeQuery = true)
    void deleteUserById(Long userId);
}
