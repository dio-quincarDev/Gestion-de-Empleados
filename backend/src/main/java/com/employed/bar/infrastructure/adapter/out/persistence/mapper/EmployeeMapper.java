package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "paymentMethodType", ignore = true)
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "bankAccountType", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    EmployeeEntity toEntity(EmployeeClass domain);

    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "attendanceRecordClasses", ignore = true)
    @Mapping(target = "consumptionClasses", ignore = true)
    EmployeeClass toDomain(EmployeeEntity entity);
}
