package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceApplicationService implements AttendanceUseCase {
    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final ReportingUseCase reportingUseCase;

    public AttendanceApplicationService(EmployeeRepositoryPort employeeRepository,
                                        AttendanceRepositoryPort attendanceRepositoryPort,
                                        ReportingUseCase reportingUseCase) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
        this.reportingUseCase = reportingUseCase;
    }

    @Override
    @Transactional
    public AttendanceRecordClass registerAttendance(AttendanceRecordClass attendanceRecord) {
        if (attendanceRecord.getEmployee() == null || attendanceRecord.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Employee ID cannot be null in AttendanceRecord");
        }
        Long employeeId = attendanceRecord.getEmployee().getId();

        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

        attendanceRecord.setEmployee(employee);
        return attendanceRepositoryPort.save(attendanceRecord);
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
