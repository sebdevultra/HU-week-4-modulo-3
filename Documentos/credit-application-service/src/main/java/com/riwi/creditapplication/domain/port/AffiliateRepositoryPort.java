package com.riwi.creditapplication.domain.port;

import com.riwi.creditapplication.domain.model.Affiliate;
import java.util.Optional;

/**
 * Output port (secondary port) for Affiliate persistence operations.
 * This interface defines the contract that infrastructure adapters must
 * implement.
 */
public interface AffiliateRepositoryPort {

    /**
     * Saves an affiliate to the repository.
     * 
     * @param affiliate the affiliate to save
     * @return the saved affiliate with generated ID
     */
    Affiliate save(Affiliate affiliate);

    /**
     * Finds an affiliate by their ID.
     * 
     * @param id the affiliate ID
     * @return an Optional containing the affiliate if found
     */
    Optional<Affiliate> findById(Long id);

    /**
     * Finds an affiliate by their document number.
     * 
     * @param documentNumber the document number
     * @return an Optional containing the affiliate if found
     */
    Optional<Affiliate> findByDocumentNumber(String documentNumber);

    /**
     * Finds an affiliate by their email.
     * 
     * @param email the email address
     * @return an Optional containing the affiliate if found
     */
    Optional<Affiliate> findByEmail(String email);

    /**
     * Checks if an affiliate exists by document number.
     * 
     * @param documentNumber the document number
     * @return true if exists, false otherwise
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Deletes an affiliate by ID.
     * 
     * @param id the affiliate ID
     */
    void deleteById(Long id);
}
