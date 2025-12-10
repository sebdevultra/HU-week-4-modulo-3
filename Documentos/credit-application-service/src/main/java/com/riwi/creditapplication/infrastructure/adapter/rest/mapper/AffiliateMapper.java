package com.riwi.creditapplication.infrastructure.adapter.rest.mapper;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.adapter.rest.AffiliateController.AffiliateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AffiliateMapper {
    AffiliateMapper INSTANCE = Mappers.getMapper(AffiliateMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "registrationDate", ignore = true)
    Affiliate toDomain(AffiliateRequest request);
}
