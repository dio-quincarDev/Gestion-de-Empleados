package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.employeeId", target = "employee")
    AttendanceRecordClass toDomain(AttendanceDto dto);

    default EmployeeClass employeeIdToEmployeeClass(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        EmployeeClass employee = new EmployeeClass();
        employee.setId(employeeId);
        return employee;
    }

    @Mapping(source = "employee.id", target = "employeeId")
    AttendanceDto toDto(AttendanceRecordClass domain);
}
