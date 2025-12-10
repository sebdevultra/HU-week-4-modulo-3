package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.RegisterAffiliateUseCase;
import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.domain.port.AffiliateRepositoryPort;

import java.time.LocalDateTime;

/**
 * Application service implementing the RegisterAffiliateUseCase.
 * This class orchestrates the affiliate registration business logic.
 */
public class RegisterAffiliateService implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    public RegisterAffiliateService(AffiliateRepositoryPort affiliateRepositoryPort) {
        this.affiliateRepositoryPort = affiliateRepositoryPort;
    }

    @Override
    public Affiliate execute(Affiliate affiliate) {
        // Validate input
        validateAffiliate(affiliate);

        // Check if affiliate already exists
        if (affiliateRepositoryPort.existsByDocumentNumber(affiliate.getDocumentNumber())) {
            throw new IllegalStateException("Affiliate with document number " +
                    affiliate.getDocumentNumber() + " already exists");
        }

        // Set registration date and default status
        affiliate.setRegistrationDate(LocalDateTime.now());
        affiliate.setStatus(Affiliate.AffiliateStatus.ACTIVE);

        // Save and return
        return affiliateRepositoryPort.save(affiliate);
    }

    private void validateAffiliate(Affiliate affiliate) {
        if (affiliate == null) {
            throw new IllegalArgumentException("Affiliate cannot be null");
        }
        if (affiliate.getFirstName() == null || affiliate.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (affiliate.getLastName() == null || affiliate.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (affiliate.getDocumentNumber() == null || affiliate.getDocumentNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Document number is required");
        }
        if (affiliate.getEmail() == null || affiliate.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
