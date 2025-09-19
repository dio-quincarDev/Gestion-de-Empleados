package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Value;

import org.springframework.util.StringUtils;

@Value
@EqualsAndHashCode(callSuper = false)
public class YappyPaymentMethod extends PaymentMethod {
    String phoneNumber;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.YAPPY;
    }

    @Override
    public void validate() {
        if (!StringUtils.hasText(phoneNumber)) {
            throw new IllegalArgumentException("Phone number is required for Yappy payment method.");
        }
    }
}
