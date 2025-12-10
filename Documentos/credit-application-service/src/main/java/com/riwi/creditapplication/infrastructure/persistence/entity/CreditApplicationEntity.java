package com.riwi.creditapplication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Credit Application.
 * Represents the persistence model for credit applications in the database.
 */
@Entity
@Table(name = "credit_applications", indexes = {
        @Index(name = "idx_credit_app_affiliate", columnList = "affiliate_id"),
        @Index(name = "idx_credit_app_status", columnList = "status"),
        @Index(name = "idx_credit_app_date", columnList = "application_date")
})
public class CreditApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relación ManyToOne con Affiliate
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "affiliate_id", nullable = false, foreignKey = @ForeignKey(name = "fk_credit_app_affiliate"))
    private AffiliateEntity affiliate;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than 0")
    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @NotNull(message = "Term in months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 360, message = "Term cannot exceed 360 months")
    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @NotBlank(message = "Purpose is required")
    @Size(min = 10, max = 500, message = "Purpose must be between 10 and 500 characters")
    @Column(name = "purpose", nullable = false, length = 500)
    private String purpose;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status;

    @Column(name = "application_date", nullable = false, updatable = false)
    private LocalDateTime applicationDate;

    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;

    @Column(name = "evaluation_comments", length = 1000)
    private String evaluationComments;

    // Relación OneToOne con RiskEvaluation
    @OneToOne(mappedBy = "creditApplication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RiskEvaluationEntity riskEvaluation;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CreditApplicationEntity() {
    }

    public CreditApplicationEntity(AffiliateEntity affiliate, BigDecimal requestedAmount,
            Integer termMonths, String purpose) {
        this.affiliate = affiliate;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = ApplicationStatus.PENDING;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.applicationDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for bidirectional relationship with RiskEvaluation
    public void setRiskEvaluation(RiskEvaluationEntity riskEvaluation) {
        if (riskEvaluation == null) {
            if (this.riskEvaluation != null) {
                this.riskEvaluation.setCreditApplication(null);
            }
        } else {
            riskEvaluation.setCreditApplication(this);
        }
        this.riskEvaluation = riskEvaluation;
    }

    // Business methods
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isUnderEvaluation() {
        return this.status == ApplicationStatus.UNDER_EVALUATION;
    }

    public boolean isApproved() {
        return this.status == ApplicationStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }

    public boolean canBeEvaluated() {
        return this.status == ApplicationStatus.PENDING ||
                this.status == ApplicationStatus.UNDER_EVALUATION;
    }

    public void approve(String comments) {
        this.status = ApplicationStatus.APPROVED;
        this.evaluationDate = LocalDateTime.now();
        this.evaluationComments = comments;
    }

    public void reject(String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.evaluationDate = LocalDateTime.now();
        this.evaluationComments = reason;
    }

    public void cancel() {
        this.status = ApplicationStatus.CANCELLED;
    }

    public void startEvaluation() {
        this.status = ApplicationStatus.UNDER_EVALUATION;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AffiliateEntity getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(AffiliateEntity affiliate) {
        this.affiliate = affiliate;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getEvaluationComments() {
        return evaluationComments;
    }

    public void setEvaluationComments(String evaluationComments) {
        this.evaluationComments = evaluationComments;
    }

    public RiskEvaluationEntity getRiskEvaluation() {
        return riskEvaluation;
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

    // Enum for Application Status
    public enum ApplicationStatus {
        PENDING,
        UNDER_EVALUATION,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CreditApplicationEntity))
            return false;
        CreditApplicationEntity that = (CreditApplicationEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CreditApplicationEntity{" +
                "id=" + id +
                ", requestedAmount=" + requestedAmount +
                ", status=" + status +
                ", applicationDate=" + applicationDate +
                '}';
    }
}
