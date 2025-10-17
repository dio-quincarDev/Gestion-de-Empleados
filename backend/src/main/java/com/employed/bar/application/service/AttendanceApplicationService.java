package com.employed.bar.application.service;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceApplicationService implements AttendanceUseCase {
    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final ScheduleRepositoryPort scheduleRepositoryPort;

    public AttendanceApplicationService(EmployeeRepositoryPort employeeRepository,
                                        AttendanceRepositoryPort attendanceRepositoryPort,
                                        ScheduleRepositoryPort scheduleRepositoryPort) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
        this.scheduleRepositoryPort = scheduleRepositoryPort;
    }

    @Override
    public AttendanceRecordClass registerAttendance(AttendanceRecordClass attendanceRecord) {
        if (attendanceRecord.getEmployee() == null || attendanceRecord.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Employee ID cannot be null in AttendanceRecord");
        }

        // Add business rule validation
        if (attendanceRecord.getEntryTime() != null && attendanceRecord.getExitTime() != null &&
            attendanceRecord.getExitTime().isBefore(attendanceRecord.getEntryTime())) {
            throw new InvalidAttendanceDataException("Exit time cannot be before entry time.");
        }

        Long employeeId = attendanceRecord.getEmployee().getId();

        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

        attendanceRecord.setEmployee(employee);

        // New logic to calculate and set status
        if (attendanceRecord.getEntryTime() != null && attendanceRecord.getDate() != null) {
            LocalDateTime startOfDay = attendanceRecord.getDate().atStartOfDay();
            LocalDateTime endOfDay = attendanceRecord.getDate().atTime(LocalTime.MAX);

            List<ScheduleClass> schedules = scheduleRepositoryPort.findByEmployeeAndDate(employee, startOfDay, endOfDay);

            if (!schedules.isEmpty()) {
                ScheduleClass schedule = schedules.get(0); // Assume the first schedule is the relevant one for the day
                if (attendanceRecord.getEntryTime().isAfter(schedule.getStartTime().toLocalTime())) {
                    attendanceRecord.setStatus(AttendanceStatus.LATE);
                } else {
                    attendanceRecord.setStatus(AttendanceStatus.PRESENT);
                }
            }
        }

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
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, startOfMonth.lengthOfMonth());

        List<AttendanceRecordClass> records = getAttendanceListByEmployeeAndDateRange(employeeId, startOfMonth, endOfMonth);

        long daysWithAttendance = records.stream()
                .map(AttendanceRecordClass::getDate)
                .distinct()
                .count();

        long totalDaysInMonth = endOfMonth.getDayOfMonth();

        if (totalDaysInMonth == 0) {
            return 0.0;
        }

        return (double) daysWithAttendance / totalDaysInMonth * 100.0;
    }

    @Override
    public List<AttendanceRecordClass> getAttendanceListByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
        return attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate);
    }
}
