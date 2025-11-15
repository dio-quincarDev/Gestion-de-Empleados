package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.payment.PaymentDetail;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.PaymentDetailEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentDetailMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "paymentMethodType", source = "paymentMethodType")
    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "bankAccountType", source = "bankAccountType")
    @Mapping(target = "percentageSplit", source = "percentageSplit")
    PaymentDetail toDomain(PaymentDetailEntity entity);

    @AfterMapping
    default void setDomainIsDefault(@MappingTarget PaymentDetail.PaymentDetailBuilder builder, PaymentDetailEntity entity) {
        builder.isDefault(entity.isDefault());
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "paymentMethodType", source = "paymentMethodType")
    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "bankAccountType", source = "bankAccountType")
    @Mapping(target = "percentageSplit", source = "percentageSplit")
    PaymentDetailEntity toEntity(PaymentDetail domain);

    @AfterMapping
    default void setEntityIsDefault(@MappingTarget PaymentDetailEntity entity, PaymentDetail domain) {
        entity.setDefault(domain.isDefault());
    }
}
