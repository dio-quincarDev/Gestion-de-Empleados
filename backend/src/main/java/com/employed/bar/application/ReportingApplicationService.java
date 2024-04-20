package com.employed.bar.application;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportingApplicationService {
   private final ReportingService reportingService;
   public byte[] generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
       return reportingService.generateEmployeeReport(employee, startDate, endDate);
   }
   public byte[]generateConsumptionReport(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate){
       return reportingService.generateConsumptionReport(consumptions, startDate, endDate);
   }
}
