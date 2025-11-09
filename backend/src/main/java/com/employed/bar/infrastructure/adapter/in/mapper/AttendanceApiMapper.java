package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class AttendanceApiMapper { // Changed to abstract class

    @Autowired
    protected EmployeeUseCase employeeUseCase; // Injected EmployeeUseCase

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "employeeId", target = "employee", qualifiedByName = "employeeIdToEmployee")
    public abstract AttendanceRecordClass toDomain(AttendanceDto dto); // Changed to abstract method

    @Named("employeeIdToEmployee")
    public EmployeeClass employeeIdToEmployeeClass(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        // Use the injected EmployeeUseCase to fetch the full EmployeeClass
        Optional<EmployeeClass> employee = employeeUseCase.getEmployeeById(employeeId);
        return employee.orElse(null); // Or throw an exception if employee must exist
    }

    @Mapping(source = "employee.id", target = "employeeId")
    public abstract AttendanceDto toDto(AttendanceRecordClass domain); // Changed to abstract method

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true) // Ignore employee during update
    public abstract void update(@MappingTarget AttendanceRecordClass attendanceRecord, AttendanceDto attendanceDto); // Changed to abstract method
}
