package com.employed.bar.infrastructure.dtos;

import com.employed.bar.domain.enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary be positive")
    private BigDecimal salary;

    @NotNull(message = "Role is required")
    private EmployeeRole role;

    @Email(message = "El formato del correo electrónico es inválido")
    private String email;

    @NotBlank(message = "El estado no puede estar en blanco")
    private String status;

}
