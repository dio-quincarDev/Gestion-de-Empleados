package com.employed.bar.infrastructure.adapter.out.persistence.entity;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentMethodType;
import com.employed.bar.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Column(name = "contact_phone", unique = true, nullable = false)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EmployeeRole role;

    @Column(name = "hourly_rate", nullable = false)
    private BigDecimal hourlyRate;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    @Column(name = "pays_overtime")
    private boolean paysOvertime;

    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_rate_type")
    private OvertimeRateType overtimeRateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

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

}
