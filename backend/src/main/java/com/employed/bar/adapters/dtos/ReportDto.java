package com.employed.bar.adapters.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
    private List<AttendanceReportDto> attendanceReports;
    private List<ConsumptionReportDto> consumptionReports;
}
