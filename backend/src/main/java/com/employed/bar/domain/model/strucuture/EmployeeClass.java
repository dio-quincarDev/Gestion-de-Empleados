package com.employed.bar.domain.model.strucuture;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.model.payment.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
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
}
