package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import java.util.List;

public interface AttendanceReportService {
    List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day);
}
