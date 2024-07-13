package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingService {
    AttendanceReportDto generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate);
    ConsumptionReportDto generateConsumptionReport(List<Consumption>consumptions, LocalDateTime startDate, LocalDateTime endDate);
}

