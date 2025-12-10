package com.riwi.creditapplication.domain.port;

import com.riwi.creditapplication.domain.model.CreditApplication;
import java.util.List;
import java.util.Optional;

/**
 * Output port (secondary port) for CreditApplication persistence operations.
 * This interface defines the contract that infrastructure adapters must
 * implement.
 */
public interface CreditApplicationRepositoryPort {

    /**
     * Saves a credit application to the repository.
     * 
     * @param creditApplication the credit application to save
     * @return the saved credit application with generated ID
     */
    CreditApplication save(CreditApplication creditApplication);

    /**
     * Finds a credit application by its ID.
     * 
     * @param id the credit application ID
     * @return an Optional containing the credit application if found
     */
    Optional<CreditApplication> findById(Long id);

    /**
     * Finds all credit applications for a specific affiliate.
     * 
     * @param affiliateId the affiliate ID
     * @return list of credit applications
     */
    List<CreditApplication> findByAffiliateId(Long affiliateId);

    /**
     * Finds all credit applications with a specific status.
     * 
     * @param status the application status
     * @return list of credit applications
     */
    List<CreditApplication> findByStatus(CreditApplication.ApplicationStatus status);

    /**
     * Updates an existing credit application.
     * 
     * @param creditApplication the credit application to update
     * @return the updated credit application
     */
    CreditApplication update(CreditApplication creditApplication);

    /**
     * Deletes a credit application by ID.
     * 
     * @param id the credit application ID
     */
    void deleteById(Long id);
}
