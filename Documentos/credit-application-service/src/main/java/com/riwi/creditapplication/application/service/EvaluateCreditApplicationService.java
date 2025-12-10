package main.java.com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.EvaluateCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.model.RiskEvaluation;
import com.riwi.creditapplication.domain.port.CreditApplicationRepositoryPort;
import com.riwi.creditapplication.domain.port.RiskEvaluationPort;

import java.time.LocalDateTime;

/**
 * Application service implementing the EvaluateCreditApplicationUseCase.
 * This class orchestrates the credit evaluation business logic.
 */
public class EvaluateCreditApplicationService implements EvaluateCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    private final RiskEvaluationPort riskEvaluationPort;

    public EvaluateCreditApplicationService(
            CreditApplicationRepositoryPort creditApplicationRepositoryPort,
            RiskEvaluationPort riskEvaluationPort) {
        this.creditApplicationRepositoryPort = creditApplicationRepositoryPort;
        this.riskEvaluationPort = riskEvaluationPort;
    }

    @Override
    public RiskEvaluation execute(Long creditApplicationId) {
        // Validate input
        if (creditApplicationId == null) {
            throw new IllegalArgumentException("Credit application ID cannot be null");
        }

        // Retrieve credit application
        CreditApplication creditApplication = creditApplicationRepositoryPort
                .findById(creditApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found"));

        // Verify application is in valid state for evaluation
        if (creditApplication.getStatus() != CreditApplication.ApplicationStatus.PENDING &&
                creditApplication.getStatus() != CreditApplication.ApplicationStatus.UNDER_EVALUATION) {
            throw new IllegalStateException("Credit application is not in a valid state for evaluation");
        }

        // Update status to under evaluation
        creditApplication.setStatus(CreditApplication.ApplicationStatus.UNDER_EVALUATION);
        creditApplicationRepositoryPort.update(creditApplication);

        // Perform risk evaluation
        RiskEvaluation riskEvaluation = riskEvaluationPort.evaluateRisk(creditApplication);

        // Update application based on evaluation result
        if (riskEvaluation.getApproved()) {
            creditApplication.setStatus(CreditApplication.ApplicationStatus.APPROVED);
            creditApplication.setEvaluationComments("Application approved with credit score: " +
                    riskEvaluation.getCreditScore());
        } else {
            creditApplication.setStatus(CreditApplication.ApplicationStatus.REJECTED);
            creditApplication.setEvaluationComments("Application rejected: " +
                    riskEvaluation.getRejectionReason());
        }

        creditApplication.setEvaluationDate(LocalDateTime.now());
        creditApplicationRepositoryPort.update(creditApplication);

        return riskEvaluation;
    }
}
