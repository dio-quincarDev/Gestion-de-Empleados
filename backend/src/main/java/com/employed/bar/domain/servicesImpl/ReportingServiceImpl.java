package com.employed.bar.domain.servicesImpl;

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
public class ReportingServiceImpl implements ReportingService{
    private final EmployeeRepository employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportingServiceImpl(EmployeeRepository employeeRepository, ConsumptionRepository consumptionRepository) {
        this.employeeRepository = employeeRepository;
        this.consumptionRepository = consumptionRepository;
    }


    @Override
    public byte[] generateEmployeeReport(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {

        Employee employeeData = employeeRepository.findById(employee.getId())
                .orElseThrow(()->new RuntimeException("Could not find employee"));
        //logica de historial de asistencia
        List<AttendanceRecord> attendanceRecords =  employeeData.getAttendanceRecords().stream()
                .filter(record -> { LocalDateTime recordDateTime = record.getDate().atTime(record.getEntryTime());
                     return recordDateTime.isAfter(startDate)&&recordDateTime.isBefore(endDate);})
                .collect(Collectors.toList());



        String reportContent = "Employee Report\n\n";
        reportContent += String.format("Name: %s\n", employeeData.getName());
        reportContent += "Attendance Records:\n";
        for (AttendanceRecord attendanceRecord : attendanceRecords) {
            reportContent += attendanceRecord.getDate() + " " + attendanceRecord.getEntryTime() + "-" + attendanceRecord.getStatus() + "\n";
        }
        return reportContent.getBytes();
    }

    @Override
    public byte[] generateConsumptionReport(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate) {
       List<Consumption>filteredConsumptions = consumptions.stream()
               .filter(consumption -> consumption.getConsumptionDate().isAfter(startDate) && consumption.getConsumptionDate().isBefore(endDate))
               .collect(Collectors.toList());

       String reportContent = "Consumption Report\n\n";
       for (Consumption consumption : filteredConsumptions) {
           reportContent += consumption.getConsumptionDate()+"-"+consumption.getEmployee().getName()+"-"+consumption.getDescription()+"\n";
       }
        return reportContent.getBytes();
    }

}
