package com.employed.bar.infrastructure.dto.report;

import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
public class ReportDto {
    private final List<AttendanceReportDto> attendanceReports;
    private final List<ConsumptionReportDto> individualConsumptionReports;

    private double totalAttendanceHours;
    private double totalConsumptionAmount;

}
