package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.GetCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.infrastructure.persistence.entity.CreditApplicationEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCreditApplicationService implements GetCreditApplicationUseCase {

    private final CreditApplicationJpaRepository repository;

    public GetCreditApplicationService(CreditApplicationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CreditApplication execute(Long id) {
        CreditApplicationEntity entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found with id: " + id));
        return toDomain(entity);
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
