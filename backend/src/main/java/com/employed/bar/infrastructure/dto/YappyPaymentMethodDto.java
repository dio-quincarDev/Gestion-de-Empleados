package com.employed.bar.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YappyPaymentMethodDto extends PaymentMethodDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
