package com.riwi.creditapplication.infrastructure.exception;

/**
 * Exception thrown when a credit request is invalid.
 */
public class InvalidCreditRequestException extends RuntimeException {

    private final String reason;

    public InvalidCreditRequestException(String message) {
        super(message);
        this.reason = message;
    }

    public InvalidCreditRequestException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
