package com.employed.bar.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AchPaymentMethodDto.class, name = "ACH"),
    @JsonSubTypes.Type(value = YappyPaymentMethodDto.class, name = "YAPPY"),
    @JsonSubTypes.Type(value = CashPaymentMethodDto.class, name = "CASH")
})
public abstract class PaymentMethodDto {
}
