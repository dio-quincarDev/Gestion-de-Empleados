package com.employed.bar.ports.out;

import com.employed.bar.adapters.dtos.AttendanceReportDto;

import java.util.List;

public interface AttendanceReportService {
    List<AttendanceReportDto> generateAttendanceReport();
}
