package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ConsumptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface ConsumptionMapper {

    @Mapping(source = "employee", target = "employee")
    ConsumptionEntity toEntity(ConsumptionClass consumptionClass);

    @Mapping(source = "employee", target = "employee")
    ConsumptionClass toDomain(ConsumptionEntity consumptionEntity);
}
