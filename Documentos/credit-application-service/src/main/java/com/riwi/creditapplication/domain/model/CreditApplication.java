package com.riwi.creditapplication.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing a Credit Application.
 * Contains all business logic related to credit application lifecycle.
 */
public class CreditApplication {

    private Long id;
    private Long affiliateId;
    private BigDecimal requestedAmount;
    private Integer termMonths;
    private String purpose;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime evaluationDate;
    private String evaluationComments;

    public CreditApplication() {
    }

    public CreditApplication(Long id, Long affiliateId, BigDecimal requestedAmount,
            Integer termMonths, String purpose, ApplicationStatus status,
            LocalDateTime applicationDate, LocalDateTime evaluationDate,
            String evaluationComments) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.requestedAmount = requestedAmount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = status;
        this.applicationDate = applicationDate;
        this.evaluationDate = evaluationDate;
        this.evaluationComments = evaluationComments;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
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

    public enum ApplicationStatus {
        PENDING,
        UNDER_EVALUATION,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}
