package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.PaymentMethodType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentDetail {
    private Long id;
    private PaymentMethodType paymentMethodType;
    private boolean isDefault;
    private String bankName;
    private String accountNumber;
    private String phoneNumber;
    private BankAccount bankAccountType;
    private BigDecimal percentageSplit;
}
