package com.riwi.creditapplication.infrastructure.adapter.rest.mapper;

import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.CreditApplicationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CreditApplicationMapper {
    CreditApplicationMapper INSTANCE = Mappers.getMapper(CreditApplicationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true) // Set by service default
    @Mapping(target = "applicationDate", ignore = true)
    @Mapping(target = "evaluationDate", ignore = true)
    @Mapping(target = "evaluationComments", ignore = true)
    CreditApplication toDomain(CreditApplicationRequest request);
}
