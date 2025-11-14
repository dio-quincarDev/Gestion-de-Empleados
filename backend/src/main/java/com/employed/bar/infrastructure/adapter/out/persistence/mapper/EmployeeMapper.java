package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.payment.*;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.PaymentDetailEntity;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "attendanceRecordClasses", ignore = true)
    @Mapping(target = "consumptionClasses", ignore = true)
    EmployeeClass toDomain(EmployeeEntity entity);

    @AfterMapping
    default void afterToDomain(@MappingTarget EmployeeClass domain, EmployeeEntity entity) {
        // Map payment method from the default payment detail
        if (entity.getPaymentDetails() != null && !entity.getPaymentDetails().isEmpty()) {
            Optional<PaymentDetailEntity> defaultPayment = entity.getPaymentDetails().stream()
                    .filter(PaymentDetailEntity::isDefault)
                    .findFirst();

            if (defaultPayment.isEmpty()) {
                // If no default, take the first one
                defaultPayment = entity.getPaymentDetails().stream().findFirst();
            }

            defaultPayment.ifPresent(pde -> {
                domain.setPaymentMethod(switch (pde.getPaymentMethodType()) {
                    case ACH -> new AchPaymentMethod(
                            pde.getBankName(),
                            pde.getAccountNumber(),
                            pde.getBankAccountType()
                    );
                    case YAPPY -> new YappyPaymentMethod(pde.getPhoneNumber());
                    case CASH -> new CashPaymentMethod();
                });
            });
        }
    }

    @Mapping(target = "paymentDetails", ignore = true)
    @Mapping(target = "user", ignore = true) // Assuming User is handled separately
    EmployeeEntity toEntity(EmployeeClass domain);

    @AfterMapping
    default void afterToEntity(@MappingTarget EmployeeEntity entity, EmployeeClass domain) {
        PaymentMethod pm = domain.getPaymentMethod();
        if (pm == null) {
            entity.setPaymentDetails(Collections.emptyList());
            return;
        }

        PaymentDetailEntity pde = new PaymentDetailEntity();
        pde.setEmployee(entity);
        pde.setDefault(true);
        pde.setPaymentMethodType(pm.getType());

        if (pm instanceof AchPaymentMethod ach) {
            pde.setBankName(ach.getBankName());
            pde.setAccountNumber(ach.getAccountNumber());
            pde.setBankAccountType(ach.getBankAccountType());
        } else if (pm instanceof YappyPaymentMethod yappy) {
            pde.setPhoneNumber(yappy.getPhoneNumber());
        }
        // For CASH, no extra fields are needed

        entity.setPaymentDetails(List.of(pde));
    }
}