package com.employed.bar.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportLine {
    private String employeeName;
    private LocalDateTime entryDateTime;
    private LocalDateTime exitDateTime;
    private BigDecimal workedHours;
    private double attendancePercentage;
}
