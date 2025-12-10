package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.CreditApplication;

/**
 * Input port (primary port) for registering a new credit application.
 * This interface defines the use case contract.
 */
public interface RegisterCreditApplicationUseCase {

    /**
     * Registers a new credit application in the system.
     * 
     * @param creditApplication the credit application data to register
     * @return the registered credit application with generated ID
     * @throws IllegalArgumentException if credit application data is invalid
     * @throws IllegalStateException    if affiliate doesn't exist or has pending
     *                                  applications
     */
    CreditApplication execute(CreditApplication creditApplication);
}
