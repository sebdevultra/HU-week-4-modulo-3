package com.riwi.creditapplication.infrastructure.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler using @ControllerAdvice.
 * Transforms exceptions into ProblemDetail (RFC 7807) responses.
 */
import io.micrometer.core.instrument.Counter;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private final Counter appErrorCounter;

        public GlobalExceptionHandler(Counter appErrorCounter) {
                this.appErrorCounter = appErrorCounter;
        }

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        private static final String TIMESTAMP_KEY = "timestamp";
        private static final String ERRORS_KEY = "errors";

        /**
         * Handle ResourceNotFoundException.
         * Returns 404 NOT FOUND.
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ProblemDetail handleResourceNotFoundException(
                        ResourceNotFoundException ex,
                        WebRequest request) {
                log.error("Resource not found: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage());

                problemDetail.setTitle("Resource Not Found");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/not-found"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                if (ex.getResourceName() != null) {
                        problemDetail.setProperty("resourceName", ex.getResourceName());
                        problemDetail.setProperty("fieldName", ex.getFieldName());
                        problemDetail.setProperty("fieldValue", ex.getFieldValue());
                }

                return problemDetail;
        }

        /**
         * Handle InvalidCreditRequestException.
         * Returns 400 BAD REQUEST.
         */
        @ExceptionHandler(InvalidCreditRequestException.class)
        public ProblemDetail handleInvalidCreditRequestException(
                        InvalidCreditRequestException ex,
                        WebRequest request) {
                log.error("Invalid credit request: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());

                problemDetail.setTitle("Invalid Credit Request");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/invalid-credit-request"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());
                problemDetail.setProperty("reason", ex.getReason());

                return problemDetail;
        }

        /**
         * Handle BusinessValidationException.
         * Returns 422 UNPROCESSABLE ENTITY.
         */
        @ExceptionHandler(BusinessValidationException.class)
        public ProblemDetail handleBusinessValidationException(
                        BusinessValidationException ex,
                        WebRequest request) {
                log.error("Business validation failed: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                ex.getMessage());

                problemDetail.setTitle("Business Validation Failed");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/business-validation"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());
                problemDetail.setProperty("validationError", ex.getValidationError());

                return problemDetail;
        }

        /**
         * Handle AuthenticationFailedException and BadCredentialsException.
         * Returns 401 UNAUTHORIZED.
         */
        @ExceptionHandler({ AuthenticationFailedException.class, BadCredentialsException.class })
        public ProblemDetail handleAuthenticationException(
                        Exception ex,
                        WebRequest request) {
                log.error("Authentication failed: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password");

                problemDetail.setTitle("Authentication Failed");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/authentication-failed"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                return problemDetail;
        }

        /**
         * Handle UsernameNotFoundException.
         * Returns 404 NOT FOUND.
         */
        @ExceptionHandler(UsernameNotFoundException.class)
        public ProblemDetail handleUsernameNotFoundException(
                        UsernameNotFoundException ex,
                        WebRequest request) {
                log.error("User not found: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage());

                problemDetail.setTitle("User Not Found");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/user-not-found"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                return problemDetail;
        }

        /**
         * Handle MethodArgumentNotValidException (Bean Validation errors).
         * Returns 400 BAD REQUEST.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ProblemDetail handleValidationException(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {
                log.error("Validation failed: {}", ex.getMessage());

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "Validation failed for one or more fields");

                problemDetail.setTitle("Validation Error");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/validation"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());
                problemDetail.setProperty(ERRORS_KEY, errors);

                return problemDetail;
        }

        /**
         * Handle IllegalArgumentException.
         * Returns 400 BAD REQUEST.
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ProblemDetail handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        WebRequest request) {
                log.error("Illegal argument: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());

                problemDetail.setTitle("Invalid Argument");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/invalid-argument"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                return problemDetail;
        }

        /**
         * Handle IllegalStateException.
         * Returns 409 CONFLICT.
         */
        @ExceptionHandler(IllegalStateException.class)
        public ProblemDetail handleIllegalStateException(
                        IllegalStateException ex,
                        WebRequest request) {
                log.error("Illegal state: {}", ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.CONFLICT,
                                ex.getMessage());

                problemDetail.setTitle("Conflict");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/conflict"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                return problemDetail;
        }

        /**
         * Handle all other exceptions.
         * Returns 500 INTERNAL SERVER ERROR.
         */
        @ExceptionHandler(Exception.class)
        public ProblemDetail handleGlobalException(
                        Exception ex,
                        WebRequest request) {
                log.error("Unexpected error occurred", ex);
                appErrorCounter.increment();

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred. Please try again later.");

                problemDetail.setTitle("Internal Server Error");
                problemDetail.setType(URI.create("https://api.creditapp.com/errors/internal-server-error"));
                problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());

                return problemDetail;
        }
}
