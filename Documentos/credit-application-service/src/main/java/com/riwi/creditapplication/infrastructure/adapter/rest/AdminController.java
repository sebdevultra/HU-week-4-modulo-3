package com.riwi.creditapplication.infrastructure.adapter.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers() {
        // Mock response for testing Admin access
        return ResponseEntity.ok(Arrays.asList("admin", "analyst", "affiliate", "user1", "user2"));
    }
}
