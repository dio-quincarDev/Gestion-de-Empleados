package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.Value;

@Value
public class AchPaymentMethod extends PaymentMethod {
    String bankName;
    String accountNumber;
    BankAccount bankAccountType;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.ACH;
    }
}
