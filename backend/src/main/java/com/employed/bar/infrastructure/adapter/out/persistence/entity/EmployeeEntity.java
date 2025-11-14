package com.employed.bar.infrastructure.adapter.out.persistence.entity;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentMethodType;
import com.employed.bar.domain.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "contact_phone", unique = true)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EmployeeRole role;

    @Column(name = "hourly_rate", nullable = false)
    private BigDecimal hourlyRate;

    @Column(name = "base_salary", nullable = false)
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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentDetailEntity> paymentDetails;

    // --- Transient fields for backward compatibility ---
    // These fields are no longer persisted directly in this table
    // but are kept for now to avoid breaking existing logic.
    // The application should be migrated to use the paymentDetails list.

    @Transient
    private PaymentMethodType paymentMethodType;

    @Transient
    private String phoneNumber; // For Yappy

    @Transient
    private String bankName; // For ACH

    @Transient
    private String accountNumber; // For ACH

    @Transient
    private BankAccount bankAccountType; // For ACH
}
