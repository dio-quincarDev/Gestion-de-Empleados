package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    AttendanceRecordClass toDomain(AttendanceDto dto);

    @Mapping(source = "employee.id", target = "employeeId")
    AttendanceDto toDto(AttendanceRecordClass domain);
}
