package com.employed.bar.domain.services;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingService {
    byte[] generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate);
    byte[]generateConsumptionReport(List<Consumption>consumptions,LocalDateTime startDate, LocalDateTime endDate);
}
