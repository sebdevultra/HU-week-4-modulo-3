package com.riwi.creditapplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing a Risk Evaluation result.
 * Contains the outcome of credit risk assessment.
 */
public class RiskEvaluation {

    private Long id;
    private Long creditApplicationId;
    private Integer creditScore;
    private RiskLevel riskLevel;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private Boolean approved;
    private String rejectionReason;
    private LocalDateTime evaluationDate;

    public RiskEvaluation() {
    }

    public RiskEvaluation(Long id, Long creditApplicationId, Integer creditScore,
            RiskLevel riskLevel, BigDecimal approvedAmount,
            BigDecimal interestRate, Boolean approved,
            String rejectionReason, LocalDateTime evaluationDate) {
        this.id = id;
        this.creditApplicationId = creditApplicationId;
        this.creditScore = creditScore;
        this.riskLevel = riskLevel;
        this.approvedAmount = approvedAmount;
        this.interestRate = interestRate;
        this.approved = approved;
        this.rejectionReason = rejectionReason;
        this.evaluationDate = evaluationDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreditApplicationId() {
        return creditApplicationId;
    }

    public void setCreditApplicationId(Long creditApplicationId) {
        this.creditApplicationId = creditApplicationId;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }
}
