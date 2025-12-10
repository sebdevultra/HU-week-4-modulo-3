package com.riwi.creditapplication.infrastructure.persistence.repository;

import com.riwi.creditapplication.infrastructure.persistence.entity.RiskEvaluationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for RiskEvaluationEntity with optimized queries.
 */
@Repository
public interface RiskEvaluationJpaRepository extends JpaRepository<RiskEvaluationEntity, Long> {

    /**
     * Find risk evaluation by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return Optional containing the risk evaluation
     */
    @Query("SELECT re FROM RiskEvaluationEntity re WHERE re.creditApplication.id = :creditApplicationId")
    Optional<RiskEvaluationEntity> findByCreditApplicationId(@Param("creditApplicationId") Long creditApplicationId);

    /**
     * Find risk evaluation by credit application ID with credit application loaded.
     *
     * @param creditApplicationId the credit application ID
     * @return Optional containing the risk evaluation with credit application
     */
    @EntityGraph(attributePaths = { "creditApplication" })
    @Query("SELECT re FROM RiskEvaluationEntity re WHERE re.creditApplication.id = :creditApplicationId")
    Optional<RiskEvaluationEntity> findByCreditApplicationIdWithApplication(
            @Param("creditApplicationId") Long creditApplicationId);

    /**
     * Find all approved risk evaluations.
     *
     * @return list of approved evaluations
     */
    @Query("SELECT re FROM RiskEvaluationEntity re WHERE re.approved = true")
    List<RiskEvaluationEntity> findAllApproved();

    /**
     * Find evaluations by risk level.
     *
     * @param riskLevel the risk level
     * @return list of risk evaluations
     */
    List<RiskEvaluationEntity> findByRiskLevel(RiskEvaluationEntity.RiskLevel riskLevel);

    /**
     * Check if risk evaluation exists for credit application.
     *
     * @param creditApplicationId the credit application ID
     * @return true if exists
     */
    boolean existsByCreditApplicationId(Long creditApplicationId);
}
