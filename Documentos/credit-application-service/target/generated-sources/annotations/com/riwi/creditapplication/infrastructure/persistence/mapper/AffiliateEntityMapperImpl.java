package com.riwi.creditapplication.infrastructure.persistence.mapper;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:56:07-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class AffiliateEntityMapperImpl implements AffiliateEntityMapper {

    @Override
    public AffiliateEntity toEntity(Affiliate domain) {
        if ( domain == null ) {
            return null;
        }

        AffiliateEntity affiliateEntity = new AffiliateEntity();

        affiliateEntity.setId( domain.getId() );
        affiliateEntity.setFirstName( domain.getFirstName() );
        affiliateEntity.setLastName( domain.getLastName() );
        affiliateEntity.setDocumentNumber( domain.getDocumentNumber() );
        affiliateEntity.setEmail( domain.getEmail() );
        affiliateEntity.setPhoneNumber( domain.getPhoneNumber() );
        affiliateEntity.setSalary( domain.getSalary() );
        affiliateEntity.setRegistrationDate( domain.getRegistrationDate() );
        affiliateEntity.setStatus( affiliateStatusToAffiliateStatus( domain.getStatus() ) );

        return affiliateEntity;
    }

    @Override
    public Affiliate toDomain(AffiliateEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Affiliate affiliate = new Affiliate();

        affiliate.setId( entity.getId() );
        affiliate.setFirstName( entity.getFirstName() );
        affiliate.setLastName( entity.getLastName() );
        affiliate.setDocumentNumber( entity.getDocumentNumber() );
        affiliate.setEmail( entity.getEmail() );
        affiliate.setPhoneNumber( entity.getPhoneNumber() );
        affiliate.setSalary( entity.getSalary() );
        affiliate.setRegistrationDate( entity.getRegistrationDate() );
        affiliate.setStatus( affiliateStatusToAffiliateStatus1( entity.getStatus() ) );

        return affiliate;
    }

    protected AffiliateEntity.AffiliateStatus affiliateStatusToAffiliateStatus(Affiliate.AffiliateStatus affiliateStatus) {
        if ( affiliateStatus == null ) {
            return null;
        }

        AffiliateEntity.AffiliateStatus affiliateStatus1;

        switch ( affiliateStatus ) {
            case ACTIVE: affiliateStatus1 = AffiliateEntity.AffiliateStatus.ACTIVE;
            break;
            case INACTIVE: affiliateStatus1 = AffiliateEntity.AffiliateStatus.INACTIVE;
            break;
            case SUSPENDED: affiliateStatus1 = AffiliateEntity.AffiliateStatus.SUSPENDED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + affiliateStatus );
        }

        return affiliateStatus1;
    }

    protected Affiliate.AffiliateStatus affiliateStatusToAffiliateStatus1(AffiliateEntity.AffiliateStatus affiliateStatus) {
        if ( affiliateStatus == null ) {
            return null;
        }

        Affiliate.AffiliateStatus affiliateStatus1;

        switch ( affiliateStatus ) {
            case ACTIVE: affiliateStatus1 = Affiliate.AffiliateStatus.ACTIVE;
            break;
            case INACTIVE: affiliateStatus1 = Affiliate.AffiliateStatus.INACTIVE;
            break;
            case SUSPENDED: affiliateStatus1 = Affiliate.AffiliateStatus.SUSPENDED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + affiliateStatus );
        }

        return affiliateStatus1;
    }
}
