package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    @Mapping(source = "employee", target = "employee")
    ScheduleEntity toEntity(ScheduleClass schedule);

    @Mapping(source = "employee", target = "employee")
    ScheduleClass toDomain(ScheduleEntity scheduleEntity);
}
