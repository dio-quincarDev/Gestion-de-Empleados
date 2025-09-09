package com.employed.bar.application.service;

import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.infrastructure.dto.AttendanceDto;
import com.employed.bar.infrastructure.dto.AttendanceReportDto;
import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceApplicationService  {
    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final AttendanceUseCase attendanceUseCase;
    private final ReportingUseCase reportingUseCase;
    private final ConsumptionRepository consumptionRepository;


    public AttendanceApplicationService(EmployeeRepositoryPort employeeRepository,
                                         AttendanceRepositoryPort attendanceRepositoryPort,
                                        AttendanceUseCase attendanceUseCase,
                                        ReportingUseCase reportingUseCase,
                                        ConsumptionRepository consumptionRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
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
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setEmployee(employee);
        attendanceRecord.setDate(attendanceDto.getDate());
        attendanceRecord.setEntryTime(attendanceDto.getEntryTime());
        attendanceRecord.setExitTime(attendanceDto.getExitTime());
        attendanceRecord.setStatus(attendanceDto.getStatus());
        return attendanceRepositoryPort.save(attendanceRecord);
    }


    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day, Long employeeId) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.atStartOfDay().toLocalDate();
        LocalDate endDate = date.atTime(LocalTime.MAX).toLocalDate();
        ReportDto report = reportingUseCase.generateCompleteReport( startDate, endDate, employeeId);
        return report.getAttendanceReports();
    }



    public double calculateAttendancePercentage(EmployeeClass employee, int year, int month, int day){
        return attendanceUseCase.calculateAttendancePercentage(employee, year, month, day);
    }

    public List<AttendanceRecord>getAttendanceListByEmployeeAndDateRange(EmployeeClass employee, LocalDate startDate, LocalDate endDate){
        return attendanceUseCase.getAttendanceListByEmployeeAndDateRange(employee, startDate, endDate);
    }
}
