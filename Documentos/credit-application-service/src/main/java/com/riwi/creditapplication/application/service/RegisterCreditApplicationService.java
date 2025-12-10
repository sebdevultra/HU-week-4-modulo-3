package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.RegisterCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.port.AffiliateRepositoryPort;
import com.riwi.creditapplication.domain.port.CreditApplicationRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Application service implementing the RegisterCreditApplicationUseCase.
 * This class orchestrates the credit application registration business logic.
 */
public class RegisterCreditApplicationService implements RegisterCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    private final AffiliateRepositoryPort affiliateRepositoryPort;

    public RegisterCreditApplicationService(
            CreditApplicationRepositoryPort creditApplicationRepositoryPort,
            AffiliateRepositoryPort affiliateRepositoryPort) {
        this.creditApplicationRepositoryPort = creditApplicationRepositoryPort;
        this.affiliateRepositoryPort = affiliateRepositoryPort;
    }

    @Override
    public CreditApplication execute(CreditApplication creditApplication) {
        // Validate input
        validateCreditApplication(creditApplication);

        // Verify affiliate exists and is active
        Affiliate affiliate = affiliateRepositoryPort.findById(creditApplication.getAffiliateId())
                .orElseThrow(() -> new IllegalStateException("Affiliate not found"));

        if (affiliate.getStatus() != Affiliate.AffiliateStatus.ACTIVE) {
            throw new IllegalStateException("Affiliate is not active");
        }

        // Check for pending applications
        List<CreditApplication> pendingApplications = creditApplicationRepositoryPort
                .findByAffiliateId(creditApplication.getAffiliateId())
                .stream()
                .filter(app -> app.getStatus() == CreditApplication.ApplicationStatus.PENDING ||
                        app.getStatus() == CreditApplication.ApplicationStatus.UNDER_EVALUATION)
                .toList();

        if (!pendingApplications.isEmpty()) {
            throw new IllegalStateException("Affiliate has pending credit applications");
        }

        // Set application date and initial status
        creditApplication.setApplicationDate(LocalDateTime.now());
        creditApplication.setStatus(CreditApplication.ApplicationStatus.PENDING);

        // Save and return
        return creditApplicationRepositoryPort.save(creditApplication);
    }

    private void validateCreditApplication(CreditApplication creditApplication) {
        if (creditApplication == null) {
            throw new IllegalArgumentException("Credit application cannot be null");
        }
        if (creditApplication.getAffiliateId() == null) {
            throw new IllegalArgumentException("Affiliate ID is required");
        }
        if (creditApplication.getRequestedAmount() == null ||
                creditApplication.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested amount must be greater than zero");
        }
        if (creditApplication.getTermMonths() == null || creditApplication.getTermMonths() <= 0) {
            throw new IllegalArgumentException("Term months must be greater than zero");
        }
        if (creditApplication.getPurpose() == null || creditApplication.getPurpose().trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose is required");
        }
    }
}
