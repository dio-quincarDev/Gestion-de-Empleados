package com.employed.bar.infrastructure.adapter.out.persistence.entity;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.PaymentMethodType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_details")
public class PaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonBackReference
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type", nullable = false)
    private PaymentMethodType paymentMethodType;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_account_type")
    private BankAccount bankAccountType;

    @Column(name = "percentage_split")
    private BigDecimal percentageSplit;
}