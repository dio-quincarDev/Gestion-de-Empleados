package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.*;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.payment.YappyPaymentMethod;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeEntity toEntity(Employee domain) {
        if (domain == null) {
            return null;
        }
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole());
        entity.setHourlyRate(domain.getHourlyRate());
        entity.setStatus(domain.getStatus());

        if (domain.getPaymentMethod() != null) {
            entity.setPaymentMethodType(domain.getPaymentMethod().getType());
            if (domain.getPaymentMethod() instanceof AchPaymentMethod ach) {
                entity.setBankName(ach.getBankName());
                entity.setAccountNumber(ach.getAccountNumber());
                entity.setBankAccountType(ach.getBankAccountType());
            } else if (domain.getPaymentMethod() instanceof YappyPaymentMethod yappy) {
                entity.setPhoneNumber(yappy.getPhoneNumber());
            }
        }

        return entity;
    }

    public Employee toDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }
        Employee domain = new Employee();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setRole(entity.getRole());
        domain.setHourlyRate(entity.getHourlyRate());
        domain.setStatus(entity.getStatus());

        if (entity.getPaymentMethodType() != null) {
            switch (entity.getPaymentMethodType()) {
                case ACH:
                    domain.setPaymentMethod(new AchPaymentMethod(
                            entity.getBankName(),
                            entity.getAccountNumber(),
                            entity.getBankAccountType()
                    ));
                    break;
                case YAPPY:
                    domain.setPaymentMethod(new YappyPaymentMethod(entity.getPhoneNumber()));
                    break;
                case CASH:
                    domain.setPaymentMethod(new CashPaymentMethod());
                    break;
            }
        }

        return domain;
    }
}