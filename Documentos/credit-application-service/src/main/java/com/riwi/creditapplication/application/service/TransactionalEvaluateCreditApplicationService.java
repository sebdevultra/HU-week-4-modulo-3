package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.EvaluateCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.RiskEvaluation;
import com.riwi.creditapplication.domain.port.RiskEvaluationPort;
import com.riwi.creditapplication.domain.service.DomainValidationService;
import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import com.riwi.creditapplication.infrastructure.persistence.entity.RiskEvaluationEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import com.riwi.creditapplication.infrastructure.persistence.repository.RiskEvaluationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Transactional service for evaluating credit applications.
 * Implements proper transaction management for critical evaluation process.
 */
@Service
@Transactional(readOnly = true)
public class TransactionalEvaluateCreditApplicationService implements EvaluateCreditApplicationUseCase {

    private final CreditApplicationJpaRepository creditApplicationRepository;
    private final RiskEvaluationJpaRepository riskEvaluationRepository;
    private final RiskEvaluationPort riskEvaluationPort;
    private final DomainValidationService validationService;

    public TransactionalEvaluateCreditApplicationService(
            CreditApplicationJpaRepository creditApplicationRepository,
            RiskEvaluationJpaRepository riskEvaluationRepository,
            RiskEvaluationPort riskEvaluationPort,
            DomainValidationService validationService) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.riskEvaluationRepository = riskEvaluationRepository;
        this.riskEvaluationPort = riskEvaluationPort;
        this.validationService = validationService;
    }

    @Override
    @Transactional // Critical write transaction
    public RiskEvaluation execute(Long creditApplicationId) {
        // Validate input
        if (creditApplicationId == null) {
            throw new IllegalArgumentException("Credit application ID cannot be null");
        }

        // Load credit application with affiliate (optimized with @EntityGraph)
        CreditApplicationEntity creditApplication = creditApplicationRepository
                .findByIdWithAffiliate(creditApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found"));

        // Validate can be evaluated
        DomainValidationService.ValidationResult validation = validationService
                .validateForEvaluation(creditApplication);

        if (!validation.isValid()) {
            throw new IllegalStateException(
                    "Cannot evaluate credit application: " + validation.getErrorMessage());
        }

        // Update status to UNDER_EVALUATION
        creditApplication.startEvaluation();
        creditApplicationRepository.save(creditApplication);

        // Call external risk evaluation service
        // Convert entity to domain model for the port
        com.riwi.creditapplication.domain.model.CreditApplication domainModel = toDomainModel(creditApplication);
        RiskEvaluation riskEvaluation = riskEvaluationPort.evaluateRisk(domainModel);

        // Create risk evaluation entity
        RiskEvaluationEntity riskEvaluationEntity = toEntity(riskEvaluation, creditApplication);

        // Save risk evaluation
        riskEvaluationEntity = riskEvaluationRepository.save(riskEvaluationEntity);

        // Update credit application based on evaluation
        if (riskEvaluation.getApproved()) {
            creditApplication.approve(
                    "Approved with credit score: " + riskEvaluation.getCreditScore() +
                            ", Risk level: " + riskEvaluation.getRiskLevel());
        } else {
            creditApplication.reject(
                    riskEvaluation.getRejectionReason() != null ? riskEvaluation.getRejectionReason()
                            : "Application rejected based on risk evaluation");
        }

        creditApplication.setEvaluationDate(LocalDateTime.now());
        creditApplicationRepository.save(creditApplication);

        // Return domain model
        return toDomain(riskEvaluationEntity);
    }

    private com.riwi.creditapplication.domain.model.CreditApplication toDomainModel(CreditApplicationEntity entity) {
        com.riwi.creditapplication.domain.model.CreditApplication domain = new com.riwi.creditapplication.domain.model.CreditApplication();
        domain.setId(entity.getId());
        domain.setAffiliateId(entity.getAffiliate().getId());
        domain.setRequestedAmount(entity.getRequestedAmount());
        domain.setTermMonths(entity.getTermMonths());
        domain.setPurpose(entity.getPurpose());
        domain.setApplicationDate(entity.getApplicationDate());

        if (entity.getStatus() != null) {
            domain.setStatus(com.riwi.creditapplication.domain.model.CreditApplication.ApplicationStatus
                    .valueOf(entity.getStatus().name()));
        }

        return domain;
    }

    private RiskEvaluationEntity toEntity(RiskEvaluation domain, CreditApplicationEntity creditApplication) {
        RiskEvaluationEntity entity = new RiskEvaluationEntity();
        entity.setCreditApplication(creditApplication);
        entity.setCreditScore(domain.getCreditScore());
        entity.setApproved(domain.getApproved());
        entity.setApprovedAmount(domain.getApprovedAmount());
        entity.setInterestRate(domain.getInterestRate());
        entity.setRejectionReason(domain.getRejectionReason());

        if (domain.getRiskLevel() != null) {
            entity.setRiskLevel(RiskEvaluationEntity.RiskLevel.valueOf(domain.getRiskLevel().name()));
        }

        return entity;
    }

    private RiskEvaluation toDomain(RiskEvaluationEntity entity) {
        RiskEvaluation domain = new RiskEvaluation();
        domain.setId(entity.getId());
        domain.setCreditApplicationId(entity.getCreditApplication().getId());
        domain.setCreditScore(entity.getCreditScore());
        domain.setApproved(entity.getApproved());
        domain.setApprovedAmount(entity.getApprovedAmount());
        domain.setInterestRate(entity.getInterestRate());
        domain.setRejectionReason(entity.getRejectionReason());
        domain.setEvaluationDate(entity.getEvaluationDate());

        if (entity.getRiskLevel() != null) {
            domain.setRiskLevel(RiskEvaluation.RiskLevel.valueOf(entity.getRiskLevel().name()));
        }

        return domain;
    }
}
