package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.payment.*;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.PaymentDetailEntity;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "attendanceRecordClasses", ignore = true)
    @Mapping(target = "consumptionClasses", ignore = true)
    EmployeeClass toDomain(EmployeeEntity entity);

    @AfterMapping
    default void afterToDomain(@MappingTarget EmployeeClass domain, EmployeeEntity entity) {
        // Use only the legacy fields from the employee entity to build paymentMethod
        // This avoids any lazy initialization issues with paymentDetails collection
        if (entity.getPaymentMethodType() != null) {
            domain.setPaymentMethod(switch (entity.getPaymentMethodType()) {
                case ACH -> new AchPaymentMethod(
                        entity.getBankName(),
                        entity.getAccountNumber(),
                        entity.getBankAccountType()
                );
                case YAPPY -> new YappyPaymentMethod(entity.getPhoneNumber());
                case CASH -> new CashPaymentMethod();
            });
        }
    }

    @Mapping(target = "paymentDetails", ignore = true)
    @Mapping(target = "user", ignore = true) // Assuming User is handled separately
    EmployeeEntity toEntity(EmployeeClass domain);

    @AfterMapping
    default void afterToEntity(@MappingTarget EmployeeEntity entity, EmployeeClass domain) {
        // Dejamos la colección de paymentDetails para ser manejada aparte
        // debido al problema con all-delete-orphan
        PaymentMethod pm = domain.getPaymentMethod();
        if (pm != null) {
            // Actualizar los campos legacy para compatibilidad
            if (pm instanceof AchPaymentMethod ach) {
                entity.setPaymentMethodType(pm.getType());
                entity.setBankName(ach.getBankName());
                entity.setAccountNumber(ach.getAccountNumber());
                entity.setBankAccountType(ach.getBankAccountType());
            } else if (pm instanceof YappyPaymentMethod yappy) {
                entity.setPaymentMethodType(pm.getType());
                entity.setPhoneNumber(yappy.getPhoneNumber());
            } else if (pm instanceof CashPaymentMethod) {
                entity.setPaymentMethodType(pm.getType());
            }
        }
    }
}