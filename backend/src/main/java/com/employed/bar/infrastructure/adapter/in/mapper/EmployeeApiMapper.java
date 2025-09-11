package com.employed.bar.infrastructure.adapter.in.mapper;

import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.payment.PaymentMethod;
import com.employed.bar.domain.model.payment.YappyPaymentMethod;
import com.employed.bar.infrastructure.dto.payment.AchPaymentMethodDto;
import com.employed.bar.infrastructure.dto.payment.CashPaymentMethodDto;
import com.employed.bar.infrastructure.dto.domain.EmployeeDto;
import com.employed.bar.infrastructure.dto.payment.PaymentMethodDto;
import com.employed.bar.infrastructure.dto.payment.YappyPaymentMethodDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeApiMapper {

    EmployeeClass toDomain(EmployeeDto dto);

    EmployeeDto toDto(EmployeeClass domain);

    default PaymentMethod toDomain(PaymentMethodDto dto) {
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

    default PaymentMethodDto toDto(PaymentMethod domain) {
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