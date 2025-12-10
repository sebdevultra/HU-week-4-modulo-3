package com.riwi.creditapplication.infrastructure.persistence.repository;

import com.riwi.creditapplication.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for UserEntity.
 * Handles user authentication and authorization queries.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find user by username.
     * Used for authentication.
     *
     * @param username the username
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Find user by email.
     *
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if username exists.
     *
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists.
     *
     * @param email the email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all enabled users.
     *
     * @return list of enabled users
     */
    @Query("SELECT u FROM UserEntity u WHERE u.enabled = true")
    List<UserEntity> findAllEnabled();

    /**
     * Find users by role.
     *
     * @param role the user role
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r = :role")
    List<UserEntity> findByRole(@Param("role") UserEntity.UserRole role);

    /**
     * Count users by role.
     *
     * @param role the user role
     * @return count of users
     */
    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") UserEntity.UserRole role);
}
