package com.employed.bar.ports.in;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.out.ReportingPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReportingAdapter implements ReportingPort {
    private final ReportingService reportingService;

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate){
        List<Employee> employee = getEmployees();
        List<Consumption> consumption = getConsumption();

        List<AttendanceReportDto> attendanceReports = employee.stream()
                .map(e -> generateEmployeeReport(e, startDate, endDate))
                .collect(Collectors.toList());

        List<ConsumptionReportDto> consumptionReports = List.of(generateConsumptionReport(consumption, startDate, endDate));

        return new ReportDto(attendanceReports, consumptionReports);
        }
        private AttendanceReportDto generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
            return reportingService.generateEmployeeReport(employee, startDate, endDate);
        }
        private ConsumptionReportDto generateConsumptionReport(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate){
            return reportingService.generateConsumptionReport(consumptions, startDate, endDate);
        }
        private List<Employee>getEmployees(){
        return List.of();
    }
    private List<Consumption>getConsumption(){
        return List.of();
    }
    public List<ReportDto> generateReport() {
        return List.of();
    }
}
