package com.riwi.creditapplication.infrastructure.persistence.mapper;

import com.riwi.creditapplication.domain.model.Affiliate;
import com.riwi.creditapplication.infrastructure.persistence.entity.AffiliateEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AffiliateEntityMapper {
    AffiliateEntityMapper INSTANCE = Mappers.getMapper(AffiliateEntityMapper.class);

    @Mapping(target = "creditApplications", ignore = true) // Domain doesn't have list
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AffiliateEntity toEntity(Affiliate domain);

    @InheritInverseConfiguration
    Affiliate toDomain(AffiliateEntity entity);
}
