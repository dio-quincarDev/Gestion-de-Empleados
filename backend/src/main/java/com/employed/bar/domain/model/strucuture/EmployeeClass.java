package com.employed.bar.domain.model.strucuture;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
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
    private EmployeeRole role;
    private BigDecimal hourlyRate;
    private PaymentMethod paymentMethod;
    private boolean paysOvertime;
    private OvertimeRateType overtimeRateType;
    private EmployeeStatus status;
    private List<com.employed.bar.domain.model.strucuture.ScheduleClass> schedules = new ArrayList<>();
    private List<AttendanceRecordClass> attendanceRecordClasses = new ArrayList<>();
    private List<ConsumptionClass> consumptionClasses = new ArrayList<>();

    public EmployeeClass(Long id, String name, String email, EmployeeRole role, BigDecimal hourlyRate, PaymentMethod paymentMethod, boolean paysOvertime, OvertimeRateType overtimeRateType, EmployeeStatus status, List<ScheduleClass> schedules, List<AttendanceRecordClass> attendanceRecordClasses, List<ConsumptionClass> consumptionClasses) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.hourlyRate = hourlyRate;
        this.paymentMethod = paymentMethod;
        this.paysOvertime = paysOvertime;
        this.overtimeRateType = overtimeRateType;
        this.status = status;
        this.schedules = schedules;
        this.attendanceRecordClasses = attendanceRecordClasses;
        this.consumptionClasses = consumptionClasses;
    }

    public void updateWith(EmployeeClass updatedEmployee) {
        this.name = updatedEmployee.getName();
        this.role = updatedEmployee.getRole();
        this.status = updatedEmployee.getStatus();
        this.email = updatedEmployee.getEmail();
        this.hourlyRate = updatedEmployee.getHourlyRate();
        if (updatedEmployee.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = updatedEmployee.getPaymentMethod();
    }
}
