package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class YappyPaymentMethod extends PaymentMethod {
    String phoneNumber;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.YAPPY;
    }
}
