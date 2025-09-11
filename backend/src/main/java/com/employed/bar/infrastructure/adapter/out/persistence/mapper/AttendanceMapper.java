package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface AttendanceMapper {
    AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

    @Mapping(source = "employee", target = "employee")
    AttendanceRecordEntity toEntity(AttendanceRecordClass attendanceRecordClass);

    @Mapping(source = "employee", target = "employee")
    AttendanceRecordClass toDomain(AttendanceRecordEntity attendanceRecordEntity);
}
