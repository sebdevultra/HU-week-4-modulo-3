package com.riwi.creditapplication.domain.service;

import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain Service for business validation rules.
 * Centralizes all domain validation logic to ensure business rules are
 * enforced.
 */
@Service
public class DomainValidationService {

    private static final BigDecimal MINIMUM_SALARY = new BigDecimal("1000000"); // 1 million COP
    private static final BigDecimal MAXIMUM_DEBT_TO_INCOME_RATIO = new BigDecimal("0.40"); // 40%
    private static final int MINIMUM_CREDIT_SCORE = 500;
    private static final int MINIMUM_AFFILIATE_SENIORITY_DAYS = 90; // 3 months
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.15"); // 15%
    private static final BigDecimal MINIMUM_CREDIT_AMOUNT = new BigDecimal("500000"); // 500k COP
    private static final BigDecimal MAXIMUM_CREDIT_AMOUNT = new BigDecimal("50000000"); // 50M COP

    /**
     * Validates if an affiliate meets all requirements to be registered.
     *
     * @param affiliate the affiliate to validate
     * @return ValidationResult with validation status and messages
     */
    public ValidationResult validateAffiliateForRegistration(AffiliateEntity affiliate) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Document must be unique (checked at repository level)
        if (!affiliate.hasValidDocument()) {
            errors.add("Document number format is invalid");
        }

        // Rule 2: Salary must be greater than 0
        if (affiliate.getSalary() == null || affiliate.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Salary must be greater than 0");
        }

        // Rule 3: Salary must meet minimum requirement
        if (affiliate.getSalary() != null &&
                affiliate.getSalary().compareTo(MINIMUM_SALARY) < 0) {
            errors.add(String.format("Salary must be at least %s", MINIMUM_SALARY));
        }

        // Rule 4: Affiliate must be active (for new registrations, set to ACTIVE by
        // default)
        if (affiliate.getStatus() == null) {
            errors.add("Affiliate status must be set");
        }

        // Rule 5: Email must be valid
        if (affiliate.getEmail() == null || !affiliate.getEmail().contains("@")) {
            errors.add("Valid email is required");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates if an affiliate can apply for credit.
     *
     * @param affiliate the affiliate applying for credit
     * @return ValidationResult with validation status and messages
     */
    public ValidationResult validateAffiliateForCreditApplication(AffiliateEntity affiliate) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Affiliate must be active
        if (!affiliate.isActive()) {
            errors.add("Affiliate must be in ACTIVE status to apply for credit");
        }

        // Rule 2: Affiliate must have sufficient salary
        if (!affiliate.hasSufficientSalary(MINIMUM_SALARY)) {
            errors.add(String.format("Affiliate salary must be at least %s", MINIMUM_SALARY));
        }

        // Rule 3: Affiliate must have valid document
        if (!affiliate.hasValidDocument()) {
            errors.add("Affiliate document is invalid");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates if a credit application meets all requirements.
     *
     * @param creditApplication the credit application to validate
     * @return ValidationResult with validation status and messages
     */
    public ValidationResult validateCreditApplication(CreditApplicationEntity creditApplication) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Requested amount must be greater than 0
        if (creditApplication.getRequestedAmount() == null ||
                creditApplication.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Requested amount must be greater than 0");
        }

        // Rule 2: Term must be valid
        if (creditApplication.getTermMonths() == null ||
                creditApplication.getTermMonths() <= 0) {
            errors.add("Term in months must be greater than 0");
        }

        // Rule 3: Purpose must be provided
        if (creditApplication.getPurpose() == null ||
                creditApplication.getPurpose().trim().isEmpty()) {
            errors.add("Purpose is required");
        }

        // Rule 4: Affiliate must exist and be valid
        if (creditApplication.getAffiliate() == null) {
            errors.add("Affiliate is required");
        } else {
            ValidationResult affiliateValidation = validateAffiliateForCreditApplication(
                    creditApplication.getAffiliate());
            if (!affiliateValidation.isValid()) {
                errors.addAll(affiliateValidation.getErrors());
            }
        }

        // Rule 5: Requested amount should not exceed salary-based limit
        if (creditApplication.getAffiliate() != null &&
                creditApplication.getRequestedAmount() != null) {
            BigDecimal maxAllowedAmount = calculateMaxAllowedAmount(
                    creditApplication.getAffiliate().getSalary());
            if (creditApplication.getRequestedAmount().compareTo(maxAllowedAmount) > 0) {
                errors.add(String.format(
                        "Requested amount exceeds maximum allowed (%s) based on salary",
                        maxAllowedAmount));
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates if a credit application can be evaluated.
     *
     * @param creditApplication the credit application to evaluate
     * @return ValidationResult with validation status and messages
     */
    public ValidationResult validateForEvaluation(CreditApplicationEntity creditApplication) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Application must exist
        if (creditApplication == null) {
            errors.add("Credit application not found");
            return new ValidationResult(false, errors);
        }

        // Rule 2: Application must be in valid status for evaluation
        if (!creditApplication.canBeEvaluated()) {
            errors.add(String.format(
                    "Credit application cannot be evaluated in status: %s",
                    creditApplication.getStatus()));
        }

        // Rule 3: Affiliate must still be active
        if (creditApplication.getAffiliate() != null &&
                !creditApplication.getAffiliate().isActive()) {
            errors.add("Affiliate is no longer active");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates if an affiliate has pending applications.
     *
     * @param affiliate the affiliate to check
     * @return true if affiliate has pending applications
     */
    public boolean hasPendingApplications(AffiliateEntity affiliate) {
        if (affiliate.getCreditApplications() == null) {
            return false;
        }

        return affiliate.getCreditApplications().stream()
                .anyMatch(app -> app.getStatus() == CreditApplicationEntity.ApplicationStatus.PENDING ||
                        app.getStatus() == CreditApplicationEntity.ApplicationStatus.UNDER_EVALUATION);
    }

    /**
     * Calculates maximum allowed credit amount based on salary.
     * Uses debt-to-income ratio of 40%.
     *
     * @param salary the affiliate's salary
     * @return maximum allowed credit amount
     */
    public BigDecimal calculateMaxAllowedAmount(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Maximum monthly payment = 40% of salary
        BigDecimal maxMonthlyPayment = salary.multiply(MAXIMUM_DEBT_TO_INCOME_RATIO);

        // Assuming average term of 24 months and 15% annual interest
        // This is a simplified calculation
        BigDecimal averageTermMonths = new BigDecimal("24");

        return maxMonthlyPayment.multiply(averageTermMonths);
    }

    /**
     * Validates document uniqueness.
     *
     * @param documentNumber    the document number to validate
     * @param existingDocuments list of existing document numbers
     * @return ValidationResult with validation status
     */
    public ValidationResult validateDocumentUniqueness(
            String documentNumber,
            List<String> existingDocuments) {
        List<String> errors = new ArrayList<>();

        if (existingDocuments.contains(documentNumber)) {
            errors.add(String.format(
                    "Document number %s is already registered",
                    documentNumber));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates credit score for approval.
     *
     * @param creditScore the credit score to validate
     * @return ValidationResult with validation status
     */
    public ValidationResult validateCreditScore(Integer creditScore) {
        List<String> errors = new ArrayList<>();

        if (creditScore == null) {
            errors.add("Credit score is required");
        } else if (creditScore < MINIMUM_CREDIT_SCORE) {
            errors.add(String.format(
                    "Credit score (%d) is below minimum required (%d)",
                    creditScore,
                    MINIMUM_CREDIT_SCORE));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * VALIDACIONES CRUZADAS COMPLEJAS
     */

    /**
     * Validates affiliate seniority (minimum 90 days).
     *
     * @param affiliate the affiliate to validate
     * @return ValidationResult with validation status
     */
    public ValidationResult validateAffiliateSeniority(AffiliateEntity affiliate) {
        List<String> errors = new ArrayList<>();

        if (affiliate.getRegistrationDate() == null) {
            errors.add("Affiliate registration date is required");
            return new ValidationResult(false, errors);
        }

        long daysSinceRegistration = java.time.temporal.ChronoUnit.DAYS.between(
                affiliate.getRegistrationDate(),
                java.time.LocalDateTime.now());

        if (daysSinceRegistration < MINIMUM_AFFILIATE_SENIORITY_DAYS) {
            errors.add(String.format(
                    "Affiliate must have at least %d days of seniority. Current: %d days",
                    MINIMUM_AFFILIATE_SENIORITY_DAYS,
                    daysSinceRegistration));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Calculates the monthly payment (cuota) for a credit application.
     *
     * @param requestedAmount the requested credit amount
     * @param termMonths      the term in months
     * @return the monthly payment amount
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal requestedAmount, Integer termMonths) {
        if (requestedAmount == null || termMonths == null || termMonths <= 0) {
            return BigDecimal.ZERO;
        }

        // Monthly interest rate
        BigDecimal monthlyRate = ANNUAL_INTEREST_RATE.divide(
                new BigDecimal("12"),
                6,
                java.math.RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            // No interest
            return requestedAmount.divide(
                    new BigDecimal(termMonths),
                    2,
                    java.math.RoundingMode.HALF_UP);
        }

        // Formula: P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(termMonths);
        BigDecimal numerator = requestedAmount.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculates the debt-to-income ratio (cuota/ingreso).
     *
     * @param monthlyPayment the monthly payment
     * @param salary         the affiliate's monthly salary
     * @return the debt-to-income ratio as a decimal (e.g., 0.35 for 35%)
     */
    public BigDecimal calculateDebtToIncomeRatio(BigDecimal monthlyPayment, BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return monthlyPayment.divide(salary, 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Validates the debt-to-income ratio for a credit application.
     *
     * @param creditApplication the credit application
     * @return ValidationResult with validation status
     */
    public ValidationResult validateDebtToIncomeRatio(CreditApplicationEntity creditApplication) {
        List<String> errors = new ArrayList<>();

        if (creditApplication.getAffiliate() == null) {
            errors.add("Affiliate is required");
            return new ValidationResult(false, errors);
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(
                creditApplication.getRequestedAmount(),
                creditApplication.getTermMonths());

        BigDecimal debtToIncomeRatio = calculateDebtToIncomeRatio(
                monthlyPayment,
                creditApplication.getAffiliate().getSalary());

        if (debtToIncomeRatio.compareTo(MAXIMUM_DEBT_TO_INCOME_RATIO) > 0) {
            errors.add(String.format(
                    "Debt-to-income ratio (%.2f%%) exceeds maximum allowed (%.2f%%). " +
                            "Monthly payment: %s, Salary: %s",
                    debtToIncomeRatio.multiply(new BigDecimal("100")),
                    MAXIMUM_DEBT_TO_INCOME_RATIO.multiply(new BigDecimal("100")),
                    monthlyPayment,
                    creditApplication.getAffiliate().getSalary()));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates credit amount limits.
     *
     * @param requestedAmount the requested amount
     * @return ValidationResult with validation status
     */
    public ValidationResult validateCreditAmountLimits(BigDecimal requestedAmount) {
        List<String> errors = new ArrayList<>();

        if (requestedAmount == null) {
            errors.add("Requested amount is required");
            return new ValidationResult(false, errors);
        }

        if (requestedAmount.compareTo(MINIMUM_CREDIT_AMOUNT) < 0) {
            errors.add(String.format(
                    "Requested amount (%s) is below minimum (%s)",
                    requestedAmount,
                    MINIMUM_CREDIT_AMOUNT));
        }

        if (requestedAmount.compareTo(MAXIMUM_CREDIT_AMOUNT) > 0) {
            errors.add(String.format(
                    "Requested amount (%s) exceeds maximum (%s)",
                    requestedAmount,
                    MAXIMUM_CREDIT_AMOUNT));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Comprehensive cross-validation for credit application.
     * Applies all complex business rules.
     *
     * @param creditApplication the credit application to validate
     * @return ValidationResult with all validation errors
     */
    public ValidationResult validateCreditApplicationWithCrossValidations(
            CreditApplicationEntity creditApplication) {
        List<String> allErrors = new ArrayList<>();

        // 1. Verify affiliate is ACTIVE
        ValidationResult affiliateValidation = validateAffiliateForCreditApplication(
                creditApplication.getAffiliate());
        if (!affiliateValidation.isValid()) {
            allErrors.addAll(affiliateValidation.getErrors());
        }

        // 2. Verify affiliate seniority
        ValidationResult seniorityValidation = validateAffiliateSeniority(
                creditApplication.getAffiliate());
        if (!seniorityValidation.isValid()) {
            allErrors.addAll(seniorityValidation.getErrors());
        }

        // 3. Validate credit amount limits
        ValidationResult amountLimitsValidation = validateCreditAmountLimits(
                creditApplication.getRequestedAmount());
        if (!amountLimitsValidation.isValid()) {
            allErrors.addAll(amountLimitsValidation.getErrors());
        }

        // 4. Calculate and validate debt-to-income ratio
        ValidationResult debtToIncomeValidation = validateDebtToIncomeRatio(creditApplication);
        if (!debtToIncomeValidation.isValid()) {
            allErrors.addAll(debtToIncomeValidation.getErrors());
        }

        // 5. Validate maximum amount based on salary
        BigDecimal maxAllowed = calculateMaxAllowedAmount(
                creditApplication.getAffiliate().getSalary());
        if (creditApplication.getRequestedAmount().compareTo(maxAllowed) > 0) {
            allErrors.add(String.format(
                    "Requested amount (%s) exceeds maximum allowed based on salary (%s)",
                    creditApplication.getRequestedAmount(),
                    maxAllowed));
        }

        // 6. Validate no pending applications
        if (hasPendingApplications(creditApplication.getAffiliate())) {
            allErrors.add("Affiliate has pending credit applications");
        }

        return new ValidationResult(allErrors.isEmpty(), allErrors);
    }

    /**
     * Inner class to represent validation results.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", errors=" + errors +
                    '}';
        }
    }
}
