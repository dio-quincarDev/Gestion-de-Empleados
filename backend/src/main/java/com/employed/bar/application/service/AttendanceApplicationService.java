package com.employed.bar.application.service;

import com.employed.bar.infrastructure.dto.AttendanceDto;
import com.employed.bar.infrastructure.dto.AttendanceReportDto;
import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.AttendanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceApplicationService  {
    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceUseCase attendanceUseCase;
    private final ReportingUseCase reportingUseCase;
    private final ConsumptionRepository consumptionRepository;


    public AttendanceApplicationService(EmployeeRepositoryPort employeeRepository,
                                        AttendanceRepository attendanceRepository,
                                        AttendanceUseCase attendanceUseCase,
                                        ReportingUseCase reportingUseCase,
                                        ConsumptionRepository consumptionRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceUseCase = attendanceUseCase;
        this.reportingUseCase = reportingUseCase;
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
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.atStartOfDay().toLocalDate();
        LocalDate endDate = date.atTime(LocalTime.MAX).toLocalDate();
        ReportDto report = reportingUseCase.generateCompleteReport( startDate, endDate, employeeId);
        return report.getAttendanceReports();
    }



    public double calculateAttendancePercentage(Employee employee, int year, int month, int day){
        return attendanceUseCase.calculateAttendancePercentage(employee, year, month, day);
    }

    public List<AttendanceRecord>getAttendanceListByEmployeeAndDateRange(Employee employee, LocalDate startDate, LocalDate endDate){
        return attendanceUseCase.getAttendanceListByEmployeeAndDateRange(employee, startDate, endDate);
    }
}
