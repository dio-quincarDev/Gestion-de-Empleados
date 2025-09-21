package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "employeeId", target = "employee.id")
    AttendanceRecordClass toDomain(AttendanceDto dto);

    @Mapping(source = "employee.id", target = "employeeId")
    AttendanceDto toDto(AttendanceRecordClass domain);
}
