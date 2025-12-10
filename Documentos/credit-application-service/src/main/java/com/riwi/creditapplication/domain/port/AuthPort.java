package com.riwi.creditapplication.domain.port;

import com.riwi.creditapplication.domain.model.User;
import java.util.Optional;

/**
 * Output port (secondary port) for Authentication operations.
 * This interface defines the contract for authentication and authorization
 * services.
 */
public interface AuthPort {

    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the password
     * @return an Optional containing the authenticated user if successful
     */
    Optional<User> authenticate(String username, String password);

    /**
     * Generates an authentication token for a user.
     * 
     * @param user the authenticated user
     * @return the generated token
     */
    String generateToken(User user);

    /**
     * Validates an authentication token.
     * 
     * @param token the token to validate
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts user information from a token.
     * 
     * @param token the authentication token
     * @return an Optional containing the user if token is valid
     */
    Optional<User> getUserFromToken(String token);

    /**
     * Registers a new user in the system.
     * 
     * @param user the user to register
     * @return the registered user
     */
    User registerUser(User user);

    /**
     * Changes a user's password.
     * 
     * @param userId      the user ID
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return true if password was changed successfully
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
