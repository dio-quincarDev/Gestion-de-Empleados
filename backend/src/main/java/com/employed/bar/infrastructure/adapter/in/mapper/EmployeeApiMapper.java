package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.*;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.payment.PaymentMethod;
import com.employed.bar.domain.model.payment.YappyPaymentMethod;
import com.employed.bar.infrastructure.dto.*;
import org.springframework.stereotype.Component;

@Component
public class EmployeeApiMapper {

    public Employee toDomain(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        Employee domain = new Employee();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setEmail(dto.getEmail());
        domain.setRole(dto.getRole());
        domain.setHourlyRate(dto.getHourlyRate());
        domain.setStatus(dto.getStatus());
        domain.setPaymentMethod(toDomain(dto.getPaymentMethod()));
        return domain;
    }

    public EmployeeDto toDto(Employee domain) {
        if (domain == null) {
            return null;
        }
        EmployeeDto dto = new EmployeeDto();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setEmail(domain.getEmail());
        dto.setRole(domain.getRole());
        dto.setHourlyRate(domain.getHourlyRate());
        dto.setStatus(domain.getStatus());
        dto.setPaymentMethod(toDto(domain.getPaymentMethod()));
        return dto;
    }

    private PaymentMethod toDomain(PaymentMethodDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto instanceof AchPaymentMethodDto achDto) {
            return new AchPaymentMethod(achDto.getBankName(), achDto.getAccountNumber(), achDto.getBankAccountType());
        } else if (dto instanceof YappyPaymentMethodDto yappyDto) {
            return new YappyPaymentMethod(yappyDto.getPhoneNumber());
        } else if (dto instanceof CashPaymentMethodDto) {
            return new CashPaymentMethod();
        }
        throw new IllegalArgumentException("Unknown PaymentMethodDto type: " + dto.getClass().getName());
    }

    private PaymentMethodDto toDto(PaymentMethod domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof AchPaymentMethod ach) {
            return new AchPaymentMethodDto(ach.getBankName(), ach.getAccountNumber(), ach.getBankAccountType());
        } else if (domain instanceof YappyPaymentMethod yappy) {
            return new YappyPaymentMethodDto(yappy.getPhoneNumber());
        } else if (domain instanceof CashPaymentMethod) {
            return new CashPaymentMethodDto();
        }
        throw new IllegalArgumentException("Unknown PaymentMethod type: " + domain.getClass().getName());
    }
}
