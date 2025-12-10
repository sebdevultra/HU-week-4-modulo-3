package com.riwi.creditapplication.infrastructure.persistence.repository;

import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for AffiliateEntity with optimized queries.
 * Uses @EntityGraph and join fetch to avoid N+1 query problems.
 */
@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, Long> {

    /**
     * Find affiliate by document number.
     * Uses index for performance.
     *
     * @param documentNumber the document number
     * @return Optional containing the affiliate if found
     */
    Optional<AffiliateEntity> findByDocumentNumber(String documentNumber);

    /**
     * Find affiliate by email.
     *
     * @param email the email address
     * @return Optional containing the affiliate if found
     */
    Optional<AffiliateEntity> findByEmail(String email);

    /**
     * Check if affiliate exists by document number.
     * More efficient than findBy when only existence check is needed.
     *
     * @param documentNumber the document number
     * @return true if exists, false otherwise
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Check if affiliate exists by email.
     *
     * @param email the email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find affiliate by ID with credit applications eagerly loaded.
     * Uses @EntityGraph to avoid N+1 problem.
     *
     * @param id the affiliate ID
     * @return Optional containing the affiliate with credit applications
     */
    @EntityGraph(attributePaths = { "creditApplications" })
    @Query("SELECT a FROM AffiliateEntity a WHERE a.id = :id")
    Optional<AffiliateEntity> findByIdWithCreditApplications(@Param("id") Long id);

    /**
     * Find affiliate by document number with credit applications.
     * Uses join fetch for optimal performance.
     *
     * @param documentNumber the document number
     * @return Optional containing the affiliate with credit applications
     */
    @Query("SELECT a FROM AffiliateEntity a " +
            "LEFT JOIN FETCH a.creditApplications " +
            "WHERE a.documentNumber = :documentNumber")
    Optional<AffiliateEntity> findByDocumentNumberWithCreditApplications(
            @Param("documentNumber") String documentNumber);

    /**
     * Find all active affiliates.
     * Uses index on status column.
     *
     * @return list of active affiliates
     */
    @Query("SELECT a FROM AffiliateEntity a WHERE a.status = 'ACTIVE'")
    List<AffiliateEntity> findAllActive();

    /**
     * Find all active affiliates with credit applications.
     * Uses @EntityGraph to load associations efficiently.
     *
     * @return list of active affiliates with their credit applications
     */
    @EntityGraph(attributePaths = { "creditApplications" })
    @Query("SELECT a FROM AffiliateEntity a WHERE a.status = 'ACTIVE'")
    List<AffiliateEntity> findAllActiveWithCreditApplications();

    /**
     * Find affiliates by status.
     *
     * @param status the affiliate status
     * @return list of affiliates with the specified status
     */
    List<AffiliateEntity> findByStatus(AffiliateEntity.AffiliateStatus status);

    /**
     * Find affiliates with salary greater than or equal to minimum.
     * Useful for credit pre-qualification.
     *
     * @param minimumSalary the minimum salary threshold
     * @return list of affiliates meeting salary requirement
     */
    @Query("SELECT a FROM AffiliateEntity a WHERE a.salary >= :minimumSalary AND a.status = 'ACTIVE'")
    List<AffiliateEntity> findBySalaryGreaterThanEqualAndActive(
            @Param("minimumSalary") BigDecimal minimumSalary);

    /**
     * Find affiliates with pending credit applications.
     * Uses join to filter affiliates with pending applications.
     *
     * @return list of affiliates with pending applications
     */
    @Query("SELECT DISTINCT a FROM AffiliateEntity a " +
            "JOIN a.creditApplications ca " +
            "WHERE ca.status IN ('PENDING', 'UNDER_EVALUATION')")
    List<AffiliateEntity> findAffiliatesWithPendingApplications();

    /**
     * Find affiliates without any credit applications.
     * Useful for marketing campaigns.
     *
     * @return list of affiliates without applications
     */
    @Query("SELECT a FROM AffiliateEntity a " +
            "WHERE a.id NOT IN (SELECT DISTINCT ca.affiliate.id FROM CreditApplicationEntity ca)")
    List<AffiliateEntity> findAffiliatesWithoutApplications();

    /**
     * Count active affiliates.
     *
     * @return count of active affiliates
     */
    @Query("SELECT COUNT(a) FROM AffiliateEntity a WHERE a.status = 'ACTIVE'")
    long countActive();

    /**
     * Find affiliates by status with credit applications loaded.
     * Optimized with @EntityGraph.
     *
     * @param status the affiliate status
     * @return list of affiliates with credit applications
     */
    @EntityGraph(attributePaths = { "creditApplications" })
    @Query("SELECT a FROM AffiliateEntity a WHERE a.status = :status")
    List<AffiliateEntity> findByStatusWithCreditApplications(
            @Param("status") AffiliateEntity.AffiliateStatus status);

    /**
     * Find affiliates registered in a date range.
     *
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return list of affiliates registered in the range
     */
    @Query("SELECT a FROM AffiliateEntity a " +
            "WHERE a.registrationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.registrationDate DESC")
    List<AffiliateEntity> findByRegistrationDateBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Search affiliates by name (first name or last name).
     * Case-insensitive search.
     *
     * @param searchTerm the search term
     * @return list of matching affiliates
     */
    @Query("SELECT a FROM AffiliateEntity a " +
            "WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AffiliateEntity> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find top affiliates by number of approved applications.
     * Useful for loyalty programs.
     *
     * @param limit maximum number of results
     * @return list of top affiliates
     */
    @Query("SELECT a FROM AffiliateEntity a " +
            "JOIN a.creditApplications ca " +
            "WHERE ca.status = 'APPROVED' " +
            "GROUP BY a " +
            "ORDER BY COUNT(ca) DESC")
    List<AffiliateEntity> findTopAffiliatesByApprovedApplications(@Param("limit") int limit);
}
