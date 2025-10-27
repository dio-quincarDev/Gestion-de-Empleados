package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.infrastructure.dto.domain.ConsumptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsumptionApiMapper {
    @Mapping(source = "employeeId", target = "employee.id")
    @Mapping(source = "date", target = "consumptionDate")
    ConsumptionClass toDomain(ConsumptionDto dto);

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "consumptionDate", target = "date")
    ConsumptionDto toDto(ConsumptionClass domain);

    List<ConsumptionDto> toDtoList(List<ConsumptionClass> domainList);
}
