package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.payment.*;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Único método toEntity - mapeo manual completo
    default EmployeeEntity toEntity(EmployeeClass domain) {
        if (domain == null) {
            return null;
        }

        EmployeeEntity entity = new EmployeeEntity();
        // Mapeo manual de todos los campos básicos
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole());
        entity.setHourlyRate(domain.getHourlyRate());
        entity.setSalary(domain.getSalary());
        entity.setPaysOvertime(domain.isPaysOvertime());
        entity.setOvertimeRateType(domain.getOvertimeRateType());
        entity.setStatus(domain.getStatus());
        entity.setPaymentType(domain.getPaymentType());

        // Mapeo del payment method
        mapPaymentMethodToEntity(entity, domain);
        return entity;
    }

    // toDomain con MapStruct + AfterMapping (que sí funciona)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "attendanceRecordClasses", ignore = true)
    @Mapping(target = "consumptionClasses", ignore = true)
    EmployeeClass toDomain(EmployeeEntity entity);

    @AfterMapping
    default void mapPaymentMethodToDomain(@MappingTarget EmployeeClass domain, EmployeeEntity entity) {
        if (entity.getPaymentMethodType() == null) return;

        domain.setPaymentMethod(switch (entity.getPaymentMethodType()) {
            case ACH -> new AchPaymentMethod(
                    entity.getBankName(),
                    entity.getAccountNumber(),
                    entity.getBankAccountType()
            );
            case YAPPY -> new YappyPaymentMethod(entity.getPhoneNumber());
            case CASH -> new CashPaymentMethod();
            default -> null;
        });
    }

    default void mapPaymentMethodToEntity(EmployeeEntity entity, EmployeeClass domain) {
        PaymentMethod pm = domain.getPaymentMethod();
        if (pm == null) return;

        entity.setPaymentMethodType(pm.getType());

        if (pm instanceof AchPaymentMethod ach) {
            entity.setBankName(ach.getBankName());
            entity.setAccountNumber(ach.getAccountNumber());
            entity.setBankAccountType(ach.getBankAccountType());
        } else if (pm instanceof YappyPaymentMethod yappy) {
            entity.setPhoneNumber(yappy.getPhoneNumber());
        }
    }
}