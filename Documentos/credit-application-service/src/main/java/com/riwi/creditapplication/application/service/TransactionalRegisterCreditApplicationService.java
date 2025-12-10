package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.RegisterCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.service.DomainValidationService;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.AffiliateJpaRepository;
import com.riwi.creditapplication.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional service for registering credit applications.
 * Implements proper transaction management and domain validations.
 */
@Service
@Transactional(readOnly = true)
public class TransactionalRegisterCreditApplicationService implements RegisterCreditApplicationUseCase {

    private final CreditApplicationJpaRepository creditApplicationRepository;
    private final AffiliateJpaRepository affiliateRepository;
    private final DomainValidationService validationService;

    public TransactionalRegisterCreditApplicationService(
            CreditApplicationJpaRepository creditApplicationRepository,
            AffiliateJpaRepository affiliateRepository,
            DomainValidationService validationService) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.affiliateRepository = affiliateRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional // Write transaction
    public CreditApplication execute(CreditApplication creditApplication) {
        // Load affiliate with credit applications to check for pending ones
        AffiliateEntity affiliate = affiliateRepository
                .findByIdWithCreditApplications(creditApplication.getAffiliateId())
                .orElseThrow(() -> new IllegalStateException("Affiliate not found"));

        // Validate affiliate can apply for credit
        DomainValidationService.ValidationResult affiliateValidation = validationService
                .validateAffiliateForCreditApplication(affiliate);

        if (!affiliateValidation.isValid()) {
            throw new IllegalStateException(
                    "Affiliate cannot apply for credit: " + affiliateValidation.getErrorMessage());
        }

        // Check for pending applications
        if (validationService.hasPendingApplications(affiliate)) {
            throw new IllegalStateException("Affiliate has pending credit applications");
        }

        // Convert to entity
        CreditApplicationEntity entity = toEntity(creditApplication, affiliate);

        // Validate credit application
        DomainValidationService.ValidationResult validation = validationService.validateCreditApplication(entity);

        if (!validation.isValid()) {
            throw new IllegalArgumentException(
                    "Credit application validation failed: " + validation.getErrorMessage());
        }

        // Save entity
        CreditApplicationEntity savedEntity = creditApplicationRepository.save(entity);

        // Convert back to domain model
        return toDomain(savedEntity);
    }

    private CreditApplicationEntity toEntity(CreditApplication domain, AffiliateEntity affiliate) {
        CreditApplicationEntity entity = new CreditApplicationEntity();
        entity.setAffiliate(affiliate);
        entity.setRequestedAmount(domain.getRequestedAmount());
        entity.setTermMonths(domain.getTermMonths());
        entity.setPurpose(domain.getPurpose());

        if (domain.getStatus() != null) {
            entity.setStatus(CreditApplicationEntity.ApplicationStatus.valueOf(domain.getStatus().name()));
        }

        return entity;
    }

    private CreditApplication toDomain(CreditApplicationEntity entity) {
        CreditApplication domain = new CreditApplication();
        domain.setId(entity.getId());
        domain.setAffiliateId(entity.getAffiliate().getId());
        domain.setRequestedAmount(entity.getRequestedAmount());
        domain.setTermMonths(entity.getTermMonths());
        domain.setPurpose(entity.getPurpose());
        domain.setApplicationDate(entity.getApplicationDate());
        domain.setEvaluationDate(entity.getEvaluationDate());
        domain.setEvaluationComments(entity.getEvaluationComments());

        if (entity.getStatus() != null) {
            domain.setStatus(CreditApplication.ApplicationStatus.valueOf(entity.getStatus().name()));
        }

        return domain;
    }
}
