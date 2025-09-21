package com.employed.bar.domain.model.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionClass {
    private Long id;
    private LocalDateTime consumptionDate;
    private String description;
    private BigDecimal amount;
    private EmployeeClass employee;

    public ConsumptionClass(EmployeeClass employee, BigDecimal amount) {
        this.employee = employee;
        this.amount = amount;
    }
}
