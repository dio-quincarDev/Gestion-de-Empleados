package com.employed.bar.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportLine {
    private String employeeName;
    private LocalDate attendanceDate;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private double workedHours;
    private double attendancePercentage;
}
