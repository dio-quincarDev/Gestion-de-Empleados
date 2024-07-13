package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.ports.out.AttendanceReportService;
import com.employed.bar.ports.out.ConsumptionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final AttendanceReportService attendanceReportService;
    private final ConsumptionReportService consumptionReportService;

    @Autowired
    public ReportController(AttendanceReportService attendanceReportService, ConsumptionReportService consumptionReportService) {
        this.attendanceReportService=attendanceReportService;
        this.consumptionReportService=consumptionReportService;
    }
    @GetMapping("/attendance")
    public List<AttendanceReportDto> getAttendanceReports(int year, int month, int day) {
        return attendanceReportService.generateAttendanceReport(year, month, day);
    }
    @GetMapping("/consumption")
    public List<ConsumptionReportDto> getConsumptionReports() {
        return consumptionReportService.generateConsumptionReport();
    }
}
