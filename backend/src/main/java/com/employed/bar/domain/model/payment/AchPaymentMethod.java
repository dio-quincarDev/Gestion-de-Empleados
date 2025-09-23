package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Value;



@Value
@EqualsAndHashCode(callSuper = false)
public class AchPaymentMethod extends PaymentMethod {
    String bankName;
    String accountNumber;
    BankAccount bankAccountType;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.ACH;
    }

    @Override
    public void validate() {
        if (bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("Bank name is required for ACH payment method.");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required for ACH payment method.");
        }
        if (bankAccountType == null) {
            throw new IllegalArgumentException("Bank account type is required for ACH payment method.");
        }
    }
}
