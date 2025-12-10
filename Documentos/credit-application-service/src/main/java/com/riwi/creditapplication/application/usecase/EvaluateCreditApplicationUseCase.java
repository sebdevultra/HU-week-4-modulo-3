package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.RiskEvaluation;

/**
 * Input port (primary port) for evaluating a credit application.
 * This interface defines the use case contract.
 */
public interface EvaluateCreditApplicationUseCase {

    /**
     * Evaluates a credit application for risk and approval.
     * This involves calling external risk assessment services and updating
     * the application status based on the evaluation result.
     * 
     * @param creditApplicationId the ID of the credit application to evaluate
     * @return the risk evaluation result
     * @throws IllegalArgumentException if credit application ID is invalid
     * @throws IllegalStateException    if credit application is not in a valid
     *                                  state for evaluation
     */
    RiskEvaluation execute(Long creditApplicationId);
}
