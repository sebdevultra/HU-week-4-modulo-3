package com.riwi.creditapplication.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Credit Application registration request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationRequest {

    private Long affiliateId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Requested amount must be greater than 0")
    private BigDecimal requestedAmount;

    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer termMonths;

    @NotNull(message = "Purpose is required")
    @Size(min = 5, max = 255, message = "Purpose must be between 5 and 255 characters")
    private String purpose;
}
