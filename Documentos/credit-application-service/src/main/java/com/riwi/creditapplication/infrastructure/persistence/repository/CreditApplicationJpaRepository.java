package com.riwi.creditapplication.infrastructure.persistence.repository;

import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for CreditApplicationEntity with optimized queries.
 * Uses @EntityGraph and join fetch to avoid N+1 query problems.
 */
@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, Long> {

    /**
     * Find credit application by ID with affiliate loaded.
     * Uses @EntityGraph to avoid N+1 problem.
     *
     * @param id the credit application ID
     * @return Optional containing the credit application with affiliate
     */
    @EntityGraph(attributePaths = { "affiliate" })
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.id = :id")
    Optional<CreditApplicationEntity> findByIdWithAffiliate(@Param("id") Long id);

    /**
     * Find credit application by ID with all relationships loaded.
     * Loads affiliate and risk evaluation in a single query.
     *
     * @param id the credit application ID
     * @return Optional containing the credit application with all relationships
     */
    @EntityGraph(attributePaths = { "affiliate", "riskEvaluation" })
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.id = :id")
    Optional<CreditApplicationEntity> findByIdWithAllRelationships(@Param("id") Long id);

    /**
     * Find credit application by ID with affiliate and risk evaluation using join
     * fetch.
     * Alternative to @EntityGraph using explicit join fetch.
     *
     * @param id the credit application ID
     * @return Optional containing the credit application
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "LEFT JOIN FETCH ca.affiliate " +
            "LEFT JOIN FETCH ca.riskEvaluation " +
            "WHERE ca.id = :id")
    Optional<CreditApplicationEntity> findByIdWithJoinFetch(@Param("id") Long id);

    /**
     * Find all credit applications for a specific affiliate.
     * Uses index on affiliate_id.
     *
     * @param affiliateId the affiliate ID
     * @return list of credit applications
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.affiliate.id = :affiliateId")
    List<CreditApplicationEntity> findByAffiliateId(@Param("affiliateId") Long affiliateId);

    /**
     * Find all credit applications for a specific affiliate with risk evaluations.
     * Optimized with @EntityGraph.
     *
     * @param affiliateId the affiliate ID
     * @return list of credit applications with risk evaluations
     */
    @EntityGraph(attributePaths = { "riskEvaluation" })
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.affiliate.id = :affiliateId")
    List<CreditApplicationEntity> findByAffiliateIdWithRiskEvaluation(@Param("affiliateId") Long affiliateId);

    /**
     * Find credit applications by status.
     * Uses index on status column.
     *
     * @param status the application status
     * @return list of credit applications
     */
    List<CreditApplicationEntity> findByStatus(CreditApplicationEntity.ApplicationStatus status);

    /**
     * Find credit applications by status with affiliate loaded.
     * Optimized with @EntityGraph.
     *
     * @param status the application status
     * @return list of credit applications with affiliates
     */
    @EntityGraph(attributePaths = { "affiliate" })
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.status = :status")
    List<CreditApplicationEntity> findByStatusWithAffiliate(
            @Param("status") CreditApplicationEntity.ApplicationStatus status);

    /**
     * Find credit applications by status with pagination.
     * Supports large result sets efficiently.
     *
     * @param status   the application status
     * @param pageable pagination information
     * @return page of credit applications
     */
    @EntityGraph(attributePaths = { "affiliate" })
    Page<CreditApplicationEntity> findByStatus(
            CreditApplicationEntity.ApplicationStatus status,
            Pageable pageable);

    /**
     * Find pending applications for a specific affiliate.
     * Useful to check if affiliate can apply for new credit.
     *
     * @param affiliateId the affiliate ID
     * @return list of pending applications
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.affiliate.id = :affiliateId " +
            "AND ca.status IN ('PENDING', 'UNDER_EVALUATION')")
    List<CreditApplicationEntity> findPendingByAffiliateId(@Param("affiliateId") Long affiliateId);

    /**
     * Check if affiliate has pending applications.
     * More efficient than loading full entities.
     *
     * @param affiliateId the affiliate ID
     * @return true if affiliate has pending applications
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END " +
            "FROM CreditApplicationEntity ca " +
            "WHERE ca.affiliate.id = :affiliateId " +
            "AND ca.status IN ('PENDING', 'UNDER_EVALUATION')")
    boolean existsPendingByAffiliateId(@Param("affiliateId") Long affiliateId);

    /**
     * Find applications by amount range.
     *
     * @param minAmount minimum requested amount
     * @param maxAmount maximum requested amount
     * @return list of credit applications in the range
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.requestedAmount BETWEEN :minAmount AND :maxAmount")
    List<CreditApplicationEntity> findByAmountRange(
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    /**
     * Find applications created in a date range.
     *
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return list of credit applications
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.applicationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ca.applicationDate DESC")
    List<CreditApplicationEntity> findByApplicationDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find applications created in a date range with affiliate.
     * Optimized with @EntityGraph.
     *
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return list of credit applications with affiliates
     */
    @EntityGraph(attributePaths = { "affiliate" })
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.applicationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ca.applicationDate DESC")
    List<CreditApplicationEntity> findByApplicationDateBetweenWithAffiliate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count applications by status.
     *
     * @param status the application status
     * @return count of applications
     */
    long countByStatus(CreditApplicationEntity.ApplicationStatus status);

    /**
     * Find applications requiring evaluation.
     * Returns applications in PENDING status ordered by date.
     *
     * @return list of applications pending evaluation
     */
    @EntityGraph(attributePaths = { "affiliate" })
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.status = 'PENDING' " +
            "ORDER BY ca.applicationDate ASC")
    List<CreditApplicationEntity> findApplicationsRequiringEvaluation();

    /**
     * Find approved applications with amount greater than threshold.
     *
     * @param threshold the amount threshold
     * @return list of approved applications
     */
    @EntityGraph(attributePaths = { "affiliate", "riskEvaluation" })
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.status = 'APPROVED' " +
            "AND ca.requestedAmount >= :threshold")
    List<CreditApplicationEntity> findApprovedWithAmountGreaterThan(
            @Param("threshold") BigDecimal threshold);

    /**
     * Find applications by affiliate document number.
     * Useful for customer service queries.
     *
     * @param documentNumber the affiliate document number
     * @return list of credit applications
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "JOIN ca.affiliate a " +
            "WHERE a.documentNumber = :documentNumber " +
            "ORDER BY ca.applicationDate DESC")
    List<CreditApplicationEntity> findByAffiliateDocumentNumber(
            @Param("documentNumber") String documentNumber);

    /**
     * Get statistics for applications in a date range.
     * Returns count, total amount, and average amount.
     *
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @param status    the application status
     * @return application statistics
     */
    @Query("SELECT COUNT(ca), SUM(ca.requestedAmount), AVG(ca.requestedAmount) " +
            "FROM CreditApplicationEntity ca " +
            "WHERE ca.applicationDate BETWEEN :startDate AND :endDate " +
            "AND ca.status = :status")
    Object[] getApplicationStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") CreditApplicationEntity.ApplicationStatus status);

    /**
     * Find applications with evaluation older than specified days.
     * Useful for identifying stale evaluations.
     *
     * @param days number of days
     * @return list of applications
     */
    @Query("SELECT ca FROM CreditApplicationEntity ca " +
            "WHERE ca.status = 'UNDER_EVALUATION' " +
            "AND ca.applicationDate < :cutoffDate")
    List<CreditApplicationEntity> findStaleEvaluations(
            @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Delete applications older than specified date.
     * Useful for data retention policies.
     *
     * @param cutoffDate the cutoff date
     */
    @Query("DELETE FROM CreditApplicationEntity ca " +
            "WHERE ca.applicationDate < :cutoffDate " +
            "AND ca.status IN ('REJECTED', 'CANCELLED')")
    void deleteOldRejectedApplications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
