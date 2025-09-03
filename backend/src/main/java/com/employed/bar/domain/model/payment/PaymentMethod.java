package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AchPaymentMethod.class, name = "ACH"),
    @JsonSubTypes.Type(value = YappyPaymentMethod.class, name = "YAPPY"),
    @JsonSubTypes.Type(value = CashPaymentMethod.class, name = "CASH")
})
public abstract class PaymentMethod {
    public abstract PaymentMethodType getType();
}
