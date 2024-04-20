package com.employed.bar.adapters.dtos;

import com.employed.bar.domain.model.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeDto extends Employee {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal salary;

    private String role;
}
