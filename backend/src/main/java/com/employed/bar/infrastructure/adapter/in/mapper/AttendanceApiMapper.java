package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AttendanceApiMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "employeeId", target = "employee", qualifiedByName = "employeeIdToEmployee") // FALTA ESTO
    AttendanceRecordClass toDomain(AttendanceDto dto);

    @Named("employeeIdToEmployee")
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

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget AttendanceRecordClass attendanceRecord, AttendanceDto attendanceDto);
}
