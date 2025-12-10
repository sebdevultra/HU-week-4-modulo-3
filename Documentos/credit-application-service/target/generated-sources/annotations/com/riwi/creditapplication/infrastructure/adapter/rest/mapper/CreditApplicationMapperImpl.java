package com.riwi.creditapplication.infrastructure.adapter.rest.mapper;

import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.CreditApplicationRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:56:07-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class CreditApplicationMapperImpl implements CreditApplicationMapper {

    @Override
    public CreditApplication toDomain(CreditApplicationRequest request) {
        if ( request == null ) {
            return null;
        }

        CreditApplication creditApplication = new CreditApplication();

        creditApplication.setAffiliateId( request.getAffiliateId() );
        creditApplication.setRequestedAmount( request.getRequestedAmount() );
        creditApplication.setTermMonths( request.getTermMonths() );
        creditApplication.setPurpose( request.getPurpose() );

        return creditApplication;
    }
}
