package com.riwi.creditapplication.infrastructure.security.service;

import com.riwi.creditapplication.infrastructure.adapter.rest.dto.AuthResponse;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.LoginRequest;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.RegisterRequest;
import com.riwi.creditapplication.infrastructure.persistence.entity.UserEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.UserJpaRepository;
import com.riwi.creditapplication.infrastructure.security.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication Service for user registration and login.
 */
@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserJpaRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user.
     *
     * @param request registration request
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Validate username and email uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Parse roles
        Set<UserEntity.UserRole> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Default role
            roles.add(UserEntity.UserRole.ROLE_AFILIADO);
        } else {
            roles = request.getRoles().stream()
                    .map(role -> UserEntity.UserRole.valueOf(role.toUpperCase()))
                    .collect(Collectors.toSet());
        }

        // Create user entity
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(roles);
        user.setEnabled(true);

        // Save user
        user = userRepository.save(user);

        // Generate tokens
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User registered successfully: {}", user.getUsername());

        return buildAuthResponse(user, jwtToken, refreshToken);
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param request login request
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // Load user
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update last login
        user.updateLastLogin();
        userRepository.save(user);

        // Generate tokens
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getUsername());

        return buildAuthResponse(user, jwtToken, refreshToken);
    }

    /**
     * Build authentication response.
     */
    private AuthResponse buildAuthResponse(UserEntity user, String jwtToken, String refreshToken) {
        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return new AuthResponse(
                jwtToken,
                refreshToken,
                user.getUsername(),
                user.getEmail(),
                roles);
    }
}
