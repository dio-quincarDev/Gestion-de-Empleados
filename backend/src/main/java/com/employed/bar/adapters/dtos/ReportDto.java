package com.employed.bar.adapters.dtos;

import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@RequiredArgsConstructor
public class ReportDto {
    private final List<AttendanceReportDto> attendanceReports;
    private final List<ConsumptionReportDto> individualConsumptionReports;

}
