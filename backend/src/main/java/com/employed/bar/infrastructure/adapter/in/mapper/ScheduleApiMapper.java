package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.strucuture.ScheduleClass;
import com.employed.bar.infrastructure.dto.domain.ScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ScheduleApiMapper {
    ScheduleApiMapper INSTANCE = Mappers.getMapper(ScheduleApiMapper.class);

    @Mapping(target = "employeeId", source = "employee.id")
    ScheduleDto toDto(ScheduleClass scheduleClass);

    @Mapping(source = "employeeId", target = "employee.id")
    @Mapping(target = "id", ignore = true)
    ScheduleClass toDomain(ScheduleDto scheduleDto);
}
