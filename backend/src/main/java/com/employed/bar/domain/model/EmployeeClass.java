package com.employed.bar.domain.model;

import com.employed.bar.domain.enums.EmployeeRole;
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
    private String status;
    private List<ScheduleClass> schedules = new ArrayList<>();
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private List<Consumption> consumptions = new ArrayList<>();
}
