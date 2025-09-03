package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.Value;

@Value
public class CashPaymentMethod extends PaymentMethod {
    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.CASH;
    }
}
