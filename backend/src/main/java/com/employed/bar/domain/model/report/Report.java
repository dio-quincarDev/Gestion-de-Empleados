package com.employed.bar.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private Long employeeId;
    private List<AttendanceReportLine> attendanceLines;
    private List<ConsumptionReportLine> consumptionLines;
    private double totalAttendanceHours;
    private BigDecimal totalConsumptionAmount;
    private BigDecimal totalEarnings;
}
