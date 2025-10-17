package com.employed.bar.infrastructure.dto.domain;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.infrastructure.dto.payment.PaymentMethodDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for employee information.
 * Used to transfer employee data between the API layer and the application layer.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Hourly rate is required")
    private BigDecimal hourlyRate;

    @NotNull(message = "Role is required")
    private EmployeeRole role;

    @Email(message = "El formato del correo electrónico es inválido")
    private String email;

    @NotNull(message = "Salary is required")
    private BigDecimal salary;

    @NotBlank(message = "El número de contacto es requerido")
    @Pattern(regexp = "^\\+[1-9]\\d{0,3}[\\s-]?\\d{6,14}$",
            message = "Formato internacional: +CódigoPaís Número. Ejemplo: +507 61234567")
    private String contactPhone = "+507 ";

    @NotBlank(message = "El estado no puede estar en blanco")
    private String status;

    @NotNull(message = "Pays overtime is required")
    private boolean paysOvertime;

    private OvertimeRateType overtimeRateType;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @Valid // This annotation ensures that the nested PaymentMethodDto is also validated.
    @NotNull(message = "Payment method is required")
    private PaymentMethodDto paymentMethod;
}
