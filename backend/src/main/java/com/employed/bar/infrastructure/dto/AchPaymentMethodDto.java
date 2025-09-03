package com.employed.bar.infrastructure.dto;

import com.employed.bar.domain.enums.BankAccount;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchPaymentMethodDto extends PaymentMethodDto {
    @NotBlank(message = "Bank name is required")
    private String bankName;
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    private BankAccount bankAccountType;
}
