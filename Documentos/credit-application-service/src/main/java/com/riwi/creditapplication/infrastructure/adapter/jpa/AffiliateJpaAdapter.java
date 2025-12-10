package com.riwi.creditapplication.infrastructure.adapter.jpa;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.domain.port.AffiliateRepositoryPort;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import com.riwi.creditapplication.infrastructure.persistence.repository.AffiliateJpaRepository;
import com.riwi.creditapplication.infrastructure.persistence.mapper.AffiliateEntityMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Adapter implementation for Affiliate persistence.
 */
@Component
public class AffiliateJpaAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository affiliateJpaRepository;
    private final AffiliateEntityMapper affiliateEntityMapper;

    public AffiliateJpaAdapter(AffiliateJpaRepository affiliateJpaRepository,
            AffiliateEntityMapper affiliateEntityMapper) {
        this.affiliateJpaRepository = affiliateJpaRepository;
        this.affiliateEntityMapper = affiliateEntityMapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = affiliateEntityMapper.toEntity(affiliate);
        AffiliateEntity savedEntity = affiliateJpaRepository.save(entity);
        return affiliateEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        return affiliateJpaRepository.findById(id)
                .map(affiliateEntityMapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByDocumentNumber(String documentNumber) {
        return affiliateJpaRepository.findByDocumentNumber(documentNumber)
                .map(affiliateEntityMapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByEmail(String email) {
        return affiliateJpaRepository.findByEmail(email)
                .map(affiliateEntityMapper::toDomain);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return affiliateJpaRepository.findByDocumentNumber(documentNumber).isPresent();
    }

    @Override
    public void deleteById(Long id) {
        affiliateJpaRepository.deleteById(id);
    }
}
