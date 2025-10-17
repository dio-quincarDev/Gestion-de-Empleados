package com.employed.bar.domain.model.structure;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.domain.model.payment.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class EmployeeClass {
    private Long id;
    private String name;
    private String email;
    private String contactPhone;
    private EmployeeRole role;
    private BigDecimal hourlyRate;
    private BigDecimal salary;
    private PaymentMethod paymentMethod;
    private boolean paysOvertime;
    private OvertimeRateType overtimeRateType;
    private EmployeeStatus status;
    private PaymentType paymentType;
    private List<ScheduleClass> schedules = new ArrayList<>();
    private List<AttendanceRecordClass> attendanceRecordClasses = new ArrayList<>();
    private List<ConsumptionClass> consumptionClasses = new ArrayList<>();

    public EmployeeClass(Long id, String name, String email, String contactPhone, EmployeeRole role, BigDecimal hourlyRate,
                         BigDecimal salary, PaymentMethod paymentMethod, boolean paysOvertime,
                         OvertimeRateType overtimeRateType, EmployeeStatus status, PaymentType paymentType,
                         List<ScheduleClass> schedules, List<AttendanceRecordClass> attendanceRecordClasses,
                         List<ConsumptionClass> consumptionClasses) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactPhone = contactPhone;
        this.role = role;
        this.hourlyRate = hourlyRate;
        this.salary = salary;
        this.paymentMethod = paymentMethod;
        this.paysOvertime = paysOvertime;
        this.overtimeRateType = overtimeRateType;
        this.status = status;
        this.paymentType = paymentType;
        this.schedules = schedules;
        this.attendanceRecordClasses = attendanceRecordClasses;
        this.consumptionClasses = consumptionClasses;
    }

    public void updateWith(EmployeeClass updatedEmployee) {
        this.name = updatedEmployee.getName();
        this.email = updatedEmployee.getEmail();
        this.contactPhone = updatedEmployee.getContactPhone();
        this.role = updatedEmployee.getRole();
        this.status = updatedEmployee.getStatus();
        this.hourlyRate = updatedEmployee.getHourlyRate();
        this.salary = updatedEmployee.getSalary();
        this.paymentType = updatedEmployee.getPaymentType();
        if (updatedEmployee.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = updatedEmployee.getPaymentMethod();
    }
}