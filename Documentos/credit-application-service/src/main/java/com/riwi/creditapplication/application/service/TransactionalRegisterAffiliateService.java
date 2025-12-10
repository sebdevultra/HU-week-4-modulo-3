package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.RegisterAffiliateUseCase;
import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.domain.service.DomainValidationService;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.AffiliateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional service for registering affiliates.
 * Implements the RegisterAffiliateUseCase with proper transaction management.
 */
@Service
@Transactional(readOnly = true)
public class TransactionalRegisterAffiliateService implements RegisterAffiliateUseCase {

    private final AffiliateJpaRepository affiliateRepository;
    private final DomainValidationService validationService;

    public TransactionalRegisterAffiliateService(
            AffiliateJpaRepository affiliateRepository,
            DomainValidationService validationService) {
        this.affiliateRepository = affiliateRepository;
        this.validationService = validationService;
    }

    /**
     * Registers a new affiliate with transactional support.
     * Uses @Transactional to ensure data consistency.
     *
     * @param affiliate the affiliate domain model
     * @return the registered affiliate
     */
    @Override
    @Transactional // Write transaction
    public Affiliate execute(Affiliate affiliate) {
        // Convert domain model to entity
        AffiliateEntity entity = toEntity(affiliate);

        // Validate business rules
        DomainValidationService.ValidationResult validation = validationService
                .validateAffiliateForRegistration(entity);

        if (!validation.isValid()) {
            throw new IllegalArgumentException(
                    "Affiliate validation failed: " + validation.getErrorMessage());
        }

        // Check document uniqueness
        if (affiliateRepository.existsByDocumentNumber(entity.getDocumentNumber())) {
            throw new IllegalStateException(
                    "Affiliate with document number " + entity.getDocumentNumber() + " already exists");
        }

        // Check email uniqueness
        if (affiliateRepository.existsByEmail(entity.getEmail())) {
            throw new IllegalStateException(
                    "Affiliate with email " + entity.getEmail() + " already exists");
        }

        // Save entity
        AffiliateEntity savedEntity = affiliateRepository.save(entity);

        // Convert back to domain model
        return toDomain(savedEntity);
    }

    /**
     * Converts domain model to JPA entity.
     */
    private AffiliateEntity toEntity(Affiliate affiliate) {
        AffiliateEntity entity = new AffiliateEntity();
        entity.setFirstName(affiliate.getFirstName());
        entity.setLastName(affiliate.getLastName());
        entity.setDocumentNumber(affiliate.getDocumentNumber());
        entity.setEmail(affiliate.getEmail());
        entity.setPhoneNumber(affiliate.getPhoneNumber());
        entity.setSalary(affiliate.getSalary());

        if (affiliate.getStatus() != null) {
            entity.setStatus(AffiliateEntity.AffiliateStatus.valueOf(affiliate.getStatus().name()));
        }

        return entity;
    }

    /**
     * Converts JPA entity to domain model.
     */
    private Affiliate toDomain(AffiliateEntity entity) {
        Affiliate affiliate = new Affiliate();
        affiliate.setId(entity.getId());
        affiliate.setFirstName(entity.getFirstName());
        affiliate.setLastName(entity.getLastName());
        affiliate.setDocumentNumber(entity.getDocumentNumber());
        affiliate.setEmail(entity.getEmail());
        affiliate.setPhoneNumber(entity.getPhoneNumber());
        affiliate.setSalary(entity.getSalary());
        affiliate.setRegistrationDate(entity.getRegistrationDate());

        if (entity.getStatus() != null) {
            affiliate.setStatus(Affiliate.AffiliateStatus.valueOf(entity.getStatus().name()));
        }

        return affiliate;
    }
}
