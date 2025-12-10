package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.Affiliate;

/**
 * Input port (primary port) for registering a new affiliate.
 * This interface defines the use case contract.
 */
public interface RegisterAffiliateUseCase {

    /**
     * Registers a new affiliate in the system.
     * 
     * @param affiliate the affiliate data to register
     * @return the registered affiliate with generated ID
     * @throws IllegalArgumentException if affiliate data is invalid
     * @throws IllegalStateException    if affiliate already exists
     */
    Affiliate execute(Affiliate affiliate);
}
