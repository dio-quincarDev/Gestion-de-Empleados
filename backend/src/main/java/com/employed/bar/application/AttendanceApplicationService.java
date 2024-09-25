package com.employed.bar.application;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.AttendanceService;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.out.AttendanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceApplicationService  {
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceService attendanceService;
    private final ReportingService reportingService;
    private final ConsumptionRepository consumptionRepository;


    public AttendanceApplicationService(EmployeeRepository employeeRepository,
                                        AttendanceRepository attendanceRepository,
                                        AttendanceService attendanceService,
                                        ReportingService reportingService,
                                        ConsumptionRepository consumptionRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceService = attendanceService;
        this.reportingService = reportingService;
        this.consumptionRepository = consumptionRepository;
    }
    @Transactional
    public AttendanceRecord registerAttendance(AttendanceDto attendanceDto) {
       Long employeeId = attendanceDto.getEmployeeId();

       if (employeeId == null){
           throw new IllegalArgumentException("Employee ID cannot be null");
       }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setEmployee(employee);
        attendanceRecord.setDate(attendanceDto.getDate());
        attendanceRecord.setEntryTime(attendanceDto.getEntryTime());
        attendanceRecord.setExitTime(attendanceDto.getExitTime());
        attendanceRecord.setStatus(attendanceDto.getStatus());

        return attendanceRepository.save(attendanceRecord);
    }
    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day, Long employeeId) {

        // Obtener empleados y consumos desde los repositorios (usando employeeRepository y consumptionRepository)
        List<Employee> employees = employeeRepository.findAll();
        List<Consumption> consumptions = consumptionRepository.findAll();

        // Crear objetos LocalDateTime con los valores proporcionados (year, month, day)
        LocalDate date = LocalDate.of(year, month, day);

        // Llamar al método generateCompleteReport con los argumentos correctos
        ReportDto report = reportingService.generateCompleteReport(date, employeeId );

        // Retornar la lista de AttendanceReportDto desde el ReportDto
        return report.getAttendanceReports();
    }

    public double calculateAttendancePercentage(Employee employee, int year, int month, int day){
        return attendanceService.calculateAttendancePercentage(employee, year, month, day);
    }
}
