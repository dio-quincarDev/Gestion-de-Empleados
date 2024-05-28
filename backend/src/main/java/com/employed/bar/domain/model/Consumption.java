package com.employed.bar.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumption_date")
    private LocalDateTime consumptionDate;

    @Column(name="description")
    private String description;

    @Column(name="amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Consumption(Employee employee, BigDecimal amount) {
        this.employee = employee;
        this.amount = amount;
    }
}
