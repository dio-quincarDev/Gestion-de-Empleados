package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class CashPaymentMethod extends PaymentMethod {
    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.CASH;
    }

    @Override
    public void validate() {
        // No validation needed for cash payment method
    }
}
