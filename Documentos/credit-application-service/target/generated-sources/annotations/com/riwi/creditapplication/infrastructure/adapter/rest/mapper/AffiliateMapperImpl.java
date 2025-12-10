package com.riwi.creditapplication.infrastructure.adapter.rest.mapper;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.adapter.rest.AffiliateController;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:56:07-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class AffiliateMapperImpl implements AffiliateMapper {

    @Override
    public Affiliate toDomain(AffiliateController.AffiliateRequest request) {
        if ( request == null ) {
            return null;
        }

        Affiliate affiliate = new Affiliate();

        affiliate.setFirstName( request.getFirstName() );
        affiliate.setLastName( request.getLastName() );
        affiliate.setDocumentNumber( request.getDocumentNumber() );
        affiliate.setEmail( request.getEmail() );
        affiliate.setPhoneNumber( request.getPhoneNumber() );
        affiliate.setSalary( request.getSalary() );

        affiliate.setStatus( Affiliate.AffiliateStatus.ACTIVE );

        return affiliate;
    }
}
