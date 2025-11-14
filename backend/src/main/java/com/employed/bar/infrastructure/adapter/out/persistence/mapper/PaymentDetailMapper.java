package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.payment.PaymentDetail;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.PaymentDetailEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDetailMapper {
    PaymentDetail toDomain(PaymentDetailEntity entity);
    PaymentDetailEntity toEntity(PaymentDetail domain);
}
