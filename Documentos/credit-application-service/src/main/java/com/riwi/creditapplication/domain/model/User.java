package com.riwi.creditapplication.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Domain entity representing a User in the system.
 * Handles authentication and authorization concerns.
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<UserRole> roles;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User() {
    }

    public User(Long id, String username, String password, String email,
            Set<UserRole> roles, Boolean enabled, LocalDateTime createdAt,
            LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public enum UserRole {
        ROLE_ADMIN,
        ROLE_ANALISTA,
        ROLE_AFILIADO
    }
}
