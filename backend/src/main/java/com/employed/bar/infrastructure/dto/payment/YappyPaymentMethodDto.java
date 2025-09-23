package com.employed.bar.infrastructure.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class YappyPaymentMethodDto extends PaymentMethodDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
