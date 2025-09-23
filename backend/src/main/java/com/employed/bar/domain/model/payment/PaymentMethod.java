package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
public abstract class PaymentMethod {
    public abstract PaymentMethodType getType();
    public abstract void validate();
}
