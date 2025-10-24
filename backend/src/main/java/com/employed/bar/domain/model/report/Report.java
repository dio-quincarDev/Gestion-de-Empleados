package com.employed.bar.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class Report {
    private Long employeeId;
    private List<AttendanceReportLine> attendanceLines;
    private List<ConsumptionReportLine> consumptionLines;
    private HoursCalculation hoursCalculation;
    private BigDecimal totalConsumptionAmount;
    private BigDecimal totalEarnings;

    public Report(Long employeeId, List<AttendanceReportLine> attendanceLines, List<ConsumptionReportLine> consumptionLines, HoursCalculation hoursCalculation, BigDecimal totalConsumptionAmount, BigDecimal totalEarnings) {
        this.employeeId = employeeId;
        this.attendanceLines = attendanceLines;
        this.consumptionLines = consumptionLines;
        this.hoursCalculation = hoursCalculation;
        this.totalConsumptionAmount = totalConsumptionAmount;
        this.totalEarnings = totalEarnings;
    }

    // Helper method to get total attendance hours for backward compatibility if needed
    public BigDecimal getTotalAttendanceHours() {
        return hoursCalculation != null ? hoursCalculation.getTotalHours() : BigDecimal.ZERO;
    }
}
