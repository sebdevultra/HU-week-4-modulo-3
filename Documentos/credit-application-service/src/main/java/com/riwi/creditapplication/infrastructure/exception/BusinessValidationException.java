package com.riwi.creditapplication.infrastructure.exception;

/**
 * Exception thrown when business validation fails.
 */
public class BusinessValidationException extends RuntimeException {

    private final String validationError;

    public BusinessValidationException(String message) {
        super(message);
        this.validationError = message;
    }

    public BusinessValidationException(String message, String validationError) {
        super(message);
        this.validationError = validationError;
    }

    public String getValidationError() {
        return validationError;
    }
}
