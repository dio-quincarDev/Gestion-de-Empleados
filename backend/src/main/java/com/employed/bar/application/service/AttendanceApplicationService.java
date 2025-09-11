package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.in.mapper.AttendanceApiMapper;
import com.employed.bar.infrastructure.dto.domain.AttendanceDto;
import com.employed.bar.infrastructure.dto.report.AttendanceReportDto;
import com.employed.bar.infrastructure.dto.report.ReportDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceApplicationService implements AttendanceUseCase {
    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final ReportingUseCase reportingUseCase;
    private final ConsumptionRepository consumptionRepository;
    private final AttendanceApiMapper attendanceApiMapper;

    public AttendanceApplicationService(EmployeeRepositoryPort employeeRepository,
                                        AttendanceRepositoryPort attendanceRepositoryPort,
                                        ReportingUseCase reportingUseCase,
                                        ConsumptionRepository consumptionRepository,
                                        AttendanceApiMapper attendanceApiMapper) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
        this.reportingUseCase = reportingUseCase;
        this.consumptionRepository = consumptionRepository;
        this.attendanceApiMapper = attendanceApiMapper;
    }

    @Transactional
    public AttendanceRecordClass registerAttendance(AttendanceDto attendanceDto) {
        Long employeeId = attendanceDto.getEmployeeId();

        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

        AttendanceRecordClass attendanceRecordClass = attendanceApiMapper.toDomain(attendanceDto);
        attendanceRecordClass.setEmployee(employee);
        return attendanceRepositoryPort.save(attendanceRecordClass);
    }


    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day, Long employeeId) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.atStartOfDay().toLocalDate();
        LocalDate endDate = date.atTime(LocalTime.MAX).toLocalDate();
        ReportDto report = reportingUseCase.generateCompleteReport(startDate, endDate, employeeId);
        return report.getAttendanceReports();
    }

    @Override
    public List<AttendanceRecordClass> findEmployeeAttendances(Long employeeId, LocalDate date) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
        return attendanceRepositoryPort.findByEmployee(employee).stream()
                .filter(record -> record.getDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public double calculateAttendancePercentage(Long employeeId, int year, int month, int day) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
        // Placeholder: A real implementation is needed.
        List<AttendanceRecordClass> records = getAttendanceListByEmployeeAndDateRange(employeeId, LocalDate.of(year, month, day), LocalDate.of(year, month, day));
        return records.isEmpty() ? 0.0 : 100.0;
    }

    @Override
    public List<AttendanceRecordClass> getAttendanceListByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
        return attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate);
    }
}
