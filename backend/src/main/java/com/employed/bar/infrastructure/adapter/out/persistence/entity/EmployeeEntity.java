package com.employed.bar.infrastructure.adapter.out.persistence.entity;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EmployeeRole role;

    @Column(name = "hourly_rate", nullable = false)
    private BigDecimal hourlyRate;

    @Column(name = "status", nullable = false)
    private String status;

    // Payment Method Fields (Flattened)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type")
    private PaymentMethodType paymentMethodType;

    @Column(name = "phone_number")
    private String phoneNumber; // For Yappy

    @Column(name = "bank_name")
    private String bankName; // For ACH

    @Column(name = "account_number")
    private String accountNumber; // For ACH

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_account_type")
    private BankAccount bankAccountType; // For ACH

    // Note: Relationships to other entities like ScheduleEntity would go here
    // but are omitted for now to focus on the current refactoring.
}
