package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.GetAffiliateCreditApplicationsUseCase;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.AffiliateJpaRepository;
import com.riwi.creditapplication.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetAffiliateCreditApplicationsService implements GetAffiliateCreditApplicationsUseCase {

    private final AffiliateJpaRepository affiliateRepository;
    private final CreditApplicationJpaRepository creditApplicationRepository;

    public GetAffiliateCreditApplicationsService(
            AffiliateJpaRepository affiliateRepository,
            CreditApplicationJpaRepository creditApplicationRepository) {
        this.affiliateRepository = affiliateRepository;
        this.creditApplicationRepository = creditApplicationRepository;
    }

    @Override
    public List<CreditApplication> execute(String email) {
        AffiliateEntity affiliate = affiliateRepository.findByEmail(email)
                .orElse(null);

        if (affiliate == null) {
            // If user is not an affiliate, return empty list or throw error.
            // Returning empty list is safer for "my applications" view.
            return Collections.emptyList();
        }

        List<CreditApplicationEntity> entities = creditApplicationRepository.findByAffiliateId(affiliate.getId());

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private CreditApplication toDomain(CreditApplicationEntity entity) {
        CreditApplication domain = new CreditApplication();
        domain.setId(entity.getId());
        if (entity.getAffiliate() != null) {
            domain.setAffiliateId(entity.getAffiliate().getId());
        }
        domain.setRequestedAmount(entity.getRequestedAmount());
        domain.setTermMonths(entity.getTermMonths());
        domain.setPurpose(entity.getPurpose());
        domain.setApplicationDate(entity.getApplicationDate());
        domain.setEvaluationDate(entity.getEvaluationDate());
        domain.setEvaluationComments(entity.getEvaluationComments());

        if (entity.getStatus() != null) {
            domain.setStatus(CreditApplication.ApplicationStatus.valueOf(entity.getStatus().name()));
        }

        return domain;
    }
}
