package com.riwi.creditapplication.infrastructure.adapter.rest.dto;

import com.riwi.creditapplication.domain.model.RiskEvaluation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Credit Application Evaluation Request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateCreditApplicationRequest {

    @NotNull(message = "Credit score is required")
    @PositiveOrZero(message = "Credit score must be positive")
    private Integer creditScore;

    @NotNull(message = "Risk level is required")
    private RiskEvaluation.RiskLevel riskLevel;

    @NotNull(message = "Approved amount is required")
    private BigDecimal approvedAmount;

    @NotNull(message = "Interest rate is required")
    private BigDecimal interestRate;

    @NotNull(message = "Approved status is required")
    private Boolean approved;

    private String rejectionReason;
}
