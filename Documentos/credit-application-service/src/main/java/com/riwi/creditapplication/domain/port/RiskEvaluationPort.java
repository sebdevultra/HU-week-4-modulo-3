package com.riwi.creditapplication.domain.port;

import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.model.RiskEvaluation;

/**
 * Output port (secondary port) for Risk Evaluation operations.
 * This interface defines the contract for external risk assessment services.
 */
public interface RiskEvaluationPort {

    /**
     * Evaluates the credit risk for a given credit application.
     * This typically involves calling an external risk assessment service.
     * 
     * @param creditApplication the credit application to evaluate
     * @return the risk evaluation result
     */
    RiskEvaluation evaluateRisk(CreditApplication creditApplication);

    /**
     * Retrieves a previously performed risk evaluation.
     * 
     * @param creditApplicationId the credit application ID
     * @return the risk evaluation if found
     */
    RiskEvaluation getRiskEvaluation(Long creditApplicationId);

    /**
     * Checks if a risk evaluation exists for a credit application.
     * 
     * @param creditApplicationId the credit application ID
     * @return true if evaluation exists, false otherwise
     */
    boolean hasRiskEvaluation(Long creditApplicationId);
}
