package com.riwi.creditapplication.infrastructure.adapter.rest;

import com.riwi.creditapplication.infrastructure.adapter.rest.dto.AuthResponse;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.LoginRequest;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.RegisterRequest;
import com.riwi.creditapplication.infrastructure.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Register a new user.
     *
     * POST /api/auth/register
     *
     * @param request registration request
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for username: {}", request.getUsername());

        AuthResponse response = authenticationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user and generate JWT token.
     *
     * POST /api/auth/login
     *
     * @param request login request
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());

        AuthResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}
