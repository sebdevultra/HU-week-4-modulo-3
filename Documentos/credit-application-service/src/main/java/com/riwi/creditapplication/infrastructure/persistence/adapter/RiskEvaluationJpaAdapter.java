package com.riwi.creditapplication.infrastructure.persistence.adapter;

import com.riwi.creditapplication.domain.model.RiskEvaluation;
import com.riwi.creditapplication.domain.port.RiskEvaluationPort;
import com.riwi.creditapplication.infrastructure.persistence.entity.RiskEvaluationEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.RiskEvaluationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Adapter for RiskEvaluation persistence operations.
 * Implements the RiskEvaluationPort interface from the domain layer.
 */
@Component
@RequiredArgsConstructor
public class RiskEvaluationJpaAdapter implements RiskEvaluationPort {

    private final RiskEvaluationJpaRepository repository;

    @Override
    public RiskEvaluation evaluateRisk(com.riwi.creditapplication.domain.model.CreditApplication creditApplication) {
        // This method should call an external risk assessment service
        // For now, we'll create a basic implementation
        // In a real scenario, this would call the risk-central microservice

        RiskEvaluation evaluation = new RiskEvaluation();
        evaluation.setCreditApplicationId(creditApplication.getId());
        evaluation.setCreditScore(650); // Placeholder
        evaluation.setRiskLevel(RiskEvaluation.RiskLevel.MEDIUM);
        evaluation.setApproved(true);
        evaluation.setApprovedAmount(creditApplication.getRequestedAmount());
        evaluation.setInterestRate(new java.math.BigDecimal("0.15"));

        return save(evaluation);
    }

    @Override
    public RiskEvaluation getRiskEvaluation(Long creditApplicationId) {
        return findByCreditApplicationId(creditApplicationId).orElse(null);
    }

    @Override
    public boolean hasRiskEvaluation(Long creditApplicationId) {
        return repository.findByCreditApplicationId(creditApplicationId).isPresent();
    }

    // Internal persistence methods
    private RiskEvaluation save(RiskEvaluation riskEvaluation) {
        RiskEvaluationEntity entity = toEntity(riskEvaluation);
        RiskEvaluationEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private Optional<RiskEvaluation> findById(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    private Optional<RiskEvaluation> findByCreditApplicationId(Long creditApplicationId) {
        return repository.findByCreditApplicationId(creditApplicationId)
                .map(this::toDomain);
    }

    /**
     * Convert domain model to entity.
     */
    private RiskEvaluationEntity toEntity(RiskEvaluation domain) {
        if (domain == null) {
            return null;
        }

        RiskEvaluationEntity entity = new RiskEvaluationEntity();
        entity.setId(domain.getId());
        entity.setCreditScore(domain.getCreditScore());

        // Map RiskLevel enum
        if (domain.getRiskLevel() != null) {
            entity.setRiskLevel(RiskEvaluationEntity.RiskLevel.valueOf(domain.getRiskLevel().name()));
        }

        entity.setApproved(domain.getApproved() != null && domain.getApproved());
        entity.setApprovedAmount(domain.getApprovedAmount());
        entity.setInterestRate(domain.getInterestRate());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setEvaluationDate(domain.getEvaluationDate());

        return entity;
    }

    /**
     * Convert entity to domain model.
     */
    private RiskEvaluation toDomain(RiskEvaluationEntity entity) {
        if (entity == null) {
            return null;
        }

        RiskEvaluation domain = new RiskEvaluation();
        domain.setId(entity.getId());
        domain.setCreditApplicationId(
                entity.getCreditApplication() != null ? entity.getCreditApplication().getId() : null);
        domain.setCreditScore(entity.getCreditScore());

        // Map RiskLevel enum
        if (entity.getRiskLevel() != null) {
            domain.setRiskLevel(RiskEvaluation.RiskLevel.valueOf(entity.getRiskLevel().name()));
        }

        domain.setApproved(entity.getApproved());
        domain.setApprovedAmount(entity.getApprovedAmount());
        domain.setInterestRate(entity.getInterestRate());
        domain.setRejectionReason(entity.getRejectionReason());
        domain.setEvaluationDate(entity.getEvaluationDate());

        return domain;
    }
}
