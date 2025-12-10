package com.riwi.creditapplication.infrastructure.adapter.rest;

import com.riwi.creditapplication.application.usecase.GetAffiliateByEmailUseCase;
import com.riwi.creditapplication.application.usecase.RegisterAffiliateUseCase;
import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.persistence.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller (Input Adapter) for Affiliate operations.
 * This adapter receives HTTP requests and delegates to use cases.
 * 
 * Note: Spring annotations will be added when integrating with Spring Boot.
 */
@RestController
@RequestMapping("/affiliates")
public class AffiliateController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final GetAffiliateByEmailUseCase getAffiliateByEmailUseCase;
    private final com.riwi.creditapplication.infrastructure.adapter.rest.mapper.AffiliateMapper affiliateMapper;

    public AffiliateController(
            RegisterAffiliateUseCase registerAffiliateUseCase,
            GetAffiliateByEmailUseCase getAffiliateByEmailUseCase,
            com.riwi.creditapplication.infrastructure.adapter.rest.mapper.AffiliateMapper affiliateMapper) {
        this.registerAffiliateUseCase = registerAffiliateUseCase;
        this.getAffiliateByEmailUseCase = getAffiliateByEmailUseCase;
        this.affiliateMapper = affiliateMapper;
    }

    /**
     * POST /api/affiliates
     * Registers a new affiliate.
     * 
     * @param affiliateRequest the affiliate registration request
     * @return the registered affiliate
     */
    @org.springframework.web.bind.annotation.PostMapping
    public Affiliate registerAffiliate(
            @org.springframework.web.bind.annotation.RequestBody AffiliateRequest affiliateRequest) {
        Affiliate affiliate = affiliateMapper.toDomain(affiliateRequest);
        return registerAffiliateUseCase.execute(affiliate);
    }

    /**
     * GET /api/affiliates/{id}
     * Retrieves an affiliate by ID.
     * 
     * @param id the affiliate ID
     * @return the affiliate
     */
    @org.springframework.web.bind.annotation.GetMapping("/{id}")
    public Affiliate getAffiliate(@org.springframework.web.bind.annotation.PathVariable Long id) {
        // Implementation will be added with additional use cases
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * GET /api/affiliates/me
     * Retrieves current affiliate.
     */
    @org.springframework.web.bind.annotation.GetMapping("/me")
    public ResponseEntity<Affiliate> getMyProfile(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Affiliate affiliate = getAffiliateByEmailUseCase.execute(user.getEmail());
        return ResponseEntity.ok(affiliate);
    }

    /**
     * DTO for affiliate registration requests.
     */
    public static class AffiliateRequest {
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String email;
        private String phoneNumber;
        private java.math.BigDecimal salary;

        // Getters and Setters
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public java.math.BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(java.math.BigDecimal salary) {
            this.salary = salary;
        }
    }
}
