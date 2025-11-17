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
        // Manejar la colección paymentDetails con protección contra lazy initialization
        try {
            // First, try to use paymentDetails collection (new approach)
            if (entity.getPaymentDetails() != null) {
                // Forzar la inicialización de la colección de forma segura
                java.util.List<PaymentDetailEntity> details = entity.getPaymentDetails();
                if (!details.isEmpty()) {
                    for (PaymentDetailEntity detail : details) {
                        if (detail.isDefault()) {
                            domain.setPaymentMethod(switch (detail.getPaymentMethodType()) {
                                case ACH -> new AchPaymentMethod(
                                        detail.getBankName(),
                                        detail.getAccountNumber(),
                                        detail.getBankAccountType()
                                );
                                case YAPPY -> new YappyPaymentMethod(detail.getPhoneNumber());
                                case CASH -> new CashPaymentMethod();
                            });
                            break; // Found the default payment method, exit loop
                        }
                    }
                }
            }
        } catch (org.hibernate.LazyInitializationException e) {
            // Si no se puede acceder a la colección lazy, usar campos legacy
            System.out.println("Could not initialize paymentDetails collection, using legacy fields: " + e.getMessage());
        }

        // Fallback a los campos legacy si no se pudo obtener de paymentDetails
        if (domain.getPaymentMethod() == null && entity.getPaymentMethodType() != null) {
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