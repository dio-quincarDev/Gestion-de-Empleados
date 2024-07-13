package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReportingServiceImpl implements ReportingService {
    private final EmployeeRepository employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportingServiceImpl(EmployeeRepository employeeRepository,ConsumptionRepository consumptionRepository) {
        this.employeeRepository = employeeRepository;
        this.consumptionRepository = consumptionRepository;
    }

    @Override
    public AttendanceReportDto generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
        validateEmployee(employee);
        return employeeRepository.findById(employee.getId())
                .map(employeeData -> {
                    List<AttendanceRecord> attendanceRecords = filteredAttendanceRecords(employeeData, startDate, endDate);
                    return new AttendanceReportDto(employeeData, attendanceRecords);
                })
                .orElseThrow(()-> new RuntimeException("Not Found Employee"));

        }


    @Override
    public ConsumptionReportDto generateConsumptionReport(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate) {
        validateConsumptions(consumptions);

        List<Consumption> filteredConsumptions = filteredConsumptions(consumptions, startDate, endDate);
        return new ConsumptionReportDto(filteredConsumptions, startDate, endDate);
    }
    private static void validateEmployee(Employee employee) {
        if (employee == null || employee.getId()==null){
            throw new IllegalArgumentException("Employee Not Found");
        }
    }
    private static void validateConsumptions(List<Consumption> consumptions) {
        if (consumptions == null || consumptions.isEmpty()){
            throw new IllegalArgumentException("Consumptions its Empty ");
        }
    }
    private List<AttendanceRecord>filteredAttendanceRecords(Employee employeeData, LocalDateTime startDate, LocalDateTime endDate) {
        return employeeData.getAttendanceRecords().stream()
                .filter(record ->{
                    LocalDateTime recordDateTime = record.getDate().atTime(record.getEntryTime());
                    return recordDateTime.isAfter(startDate)&&recordDateTime.isBefore(endDate);
                })
               .collect(Collectors.toList());
    }
    private List<Consumption> filteredConsumptions(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptions.stream()
                .filter(consumption -> consumption.getConsumptionDate().isAfter(startDate) && consumption.getConsumptionDate().isBefore(endDate))
                .collect(Collectors.toList());
    }

    }







