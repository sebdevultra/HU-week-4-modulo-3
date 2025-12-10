package com.riwi.creditapplication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Risk Evaluation.
 * Represents the persistence model for risk evaluation results in the database.
 */
@Entity
@Table(name = "risk_evaluations", indexes = {
        @Index(name = "idx_risk_eval_credit_app", columnList = "credit_application_id", unique = true),
        @Index(name = "idx_risk_eval_approved", columnList = "approved"),
        @Index(name = "idx_risk_eval_risk_level", columnList = "risk_level")
})
public class RiskEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relación OneToOne con CreditApplication
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_application_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_risk_eval_credit_app"))
    private CreditApplicationEntity creditApplication;

    @NotNull(message = "Credit score is required")
    @Min(value = 0, message = "Credit score must be at least 0")
    @Max(value = 1000, message = "Credit score cannot exceed 1000")
    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;

    @NotNull(message = "Risk level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @DecimalMin(value = "0.00", message = "Approved amount must be at least 0")
    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @DecimalMin(value = "0.00", message = "Interest rate must be at least 0")
    @DecimalMax(value = "100.00", message = "Interest rate cannot exceed 100")
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull(message = "Approved status is required")
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "evaluation_date", nullable = false, updatable = false)
    private LocalDateTime evaluationDate;

    @Column(name = "debt_to_income_ratio", precision = 5, scale = 2)
    private BigDecimal debtToIncomeRatio;

    @Column(name = "payment_history_score")
    private Integer paymentHistoryScore;

    @Column(name = "external_bureau_score")
    private Integer externalBureauScore;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public RiskEvaluationEntity() {
    }

    public RiskEvaluationEntity(CreditApplicationEntity creditApplication, Integer creditScore,
            RiskLevel riskLevel, Boolean approved) {
        this.creditApplication = creditApplication;
        this.creditScore = creditScore;
        this.riskLevel = riskLevel;
        this.approved = approved;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.evaluationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isLowRisk() {
        return this.riskLevel == RiskLevel.LOW;
    }

    public boolean isMediumRisk() {
        return this.riskLevel == RiskLevel.MEDIUM;
    }

    public boolean isHighRisk() {
        return this.riskLevel == RiskLevel.HIGH || this.riskLevel == RiskLevel.VERY_HIGH;
    }

    public boolean hasGoodCreditScore() {
        return this.creditScore >= 700;
    }

    public boolean requiresManualReview() {
        return this.riskLevel == RiskLevel.HIGH ||
                (this.riskLevel == RiskLevel.MEDIUM && this.creditScore < 650);
    }

    public BigDecimal calculateMonthlyPayment() {
        if (approvedAmount == null || interestRate == null ||
                creditApplication == null || creditApplication.getTermMonths() == null) {
            return BigDecimal.ZERO;
        }

        // Simple interest calculation (can be enhanced with compound interest)
        BigDecimal monthlyRate = interestRate.divide(
                BigDecimal.valueOf(100 * 12), 6, BigDecimal.ROUND_HALF_UP);

        int months = creditApplication.getTermMonths();

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return approvedAmount.divide(BigDecimal.valueOf(months), 2, BigDecimal.ROUND_HALF_UP);
        }

        // Formula: P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(months);
        BigDecimal numerator = approvedAmount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditApplicationEntity getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplicationEntity creditApplication) {
        this.creditApplication = creditApplication;
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

    public BigDecimal getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public Integer getPaymentHistoryScore() {
        return paymentHistoryScore;
    }

    public void setPaymentHistoryScore(Integer paymentHistoryScore) {
        this.paymentHistoryScore = paymentHistoryScore;
    }

    public Integer getExternalBureauScore() {
        return externalBureauScore;
    }

    public void setExternalBureauScore(Integer externalBureauScore) {
        this.externalBureauScore = externalBureauScore;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Enum for Risk Level
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RiskEvaluationEntity))
            return false;
        RiskEvaluationEntity that = (RiskEvaluationEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RiskEvaluationEntity{" +
                "id=" + id +
                ", creditScore=" + creditScore +
                ", riskLevel=" + riskLevel +
                ", approved=" + approved +
                '}';
    }
}
