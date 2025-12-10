package com.riwi.creditapplication.application.service;

import com.riwi.creditapplication.application.usecase.GetAffiliateByEmailUseCase;
import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.AffiliateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAffiliateByEmailService implements GetAffiliateByEmailUseCase {

    private final AffiliateJpaRepository affiliateRepository;

    public GetAffiliateByEmailService(AffiliateJpaRepository affiliateRepository) {
        this.affiliateRepository = affiliateRepository;
    }

    @Override
    public Affiliate execute(String email) {
        AffiliateEntity entity = affiliateRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found with email: " + email));
        return toDomain(entity);
    }

    private Affiliate toDomain(AffiliateEntity entity) {
        Affiliate domain = new Affiliate();
        domain.setId(entity.getId());
        domain.setFirstName(entity.getFirstName());
        domain.setLastName(entity.getLastName());
        domain.setDocumentNumber(entity.getDocumentNumber());
        domain.setEmail(entity.getEmail());
        domain.setPhoneNumber(entity.getPhoneNumber());
        domain.setSalary(entity.getSalary());
        if (entity.getStatus() != null) {
            domain.setStatus(Affiliate.AffiliateStatus.valueOf(entity.getStatus().name()));
        }
        return domain;
    }
}
