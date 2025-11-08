package com.employed.bar.application.service;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.employed.bar.domain.exceptions.AttendanceNotFoundException;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

        // Updated business rule validation for LocalDateTime
        if (attendanceRecord.getEntryDateTime() != null && attendanceRecord.getExitDateTime() != null &&
            attendanceRecord.getExitDateTime().isBefore(attendanceRecord.getEntryDateTime())) {
            throw new InvalidAttendanceDataException("Exit time cannot be before entry time.");
        }

        Long employeeId = attendanceRecord.getEmployee().getId();

        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));

        attendanceRecord.setEmployee(employee);

        // Robust logic to find the correct schedule, especially for overnight shifts.
        if (attendanceRecord.getEntryDateTime() != null) {
            System.out.println("DEBUG: Entering status calculation logic.");
            LocalDate attendanceDate = attendanceRecord.getEntryDateTime().toLocalDate();
            // We define a 48-hour search window to catch schedules that cross midnight.
            LocalDateTime searchStart = attendanceDate.atStartOfDay().minusDays(1); // Search from the previous day to catch overnight schedules
            LocalDateTime searchEnd = attendanceDate.atStartOfDay().plusDays(2); // Until the end of the next day

            List<ScheduleClass> potentialSchedules = scheduleRepositoryPort.findByEmployeeAndDate(employee, searchStart, searchEnd);
            System.out.println("DEBUG: Found " + potentialSchedules.size() + " potential schedules in 48h window.");

            // Find the schedule whose start time is closest to the employee's clock-in time.
            Optional<ScheduleClass> relevantScheduleOpt = potentialSchedules.stream()
                    .min(Comparator.comparing(s ->
                            java.time.Duration.between(s.getStartTime(), attendanceRecord.getEntryDateTime()).abs()
                    ));

            if (relevantScheduleOpt.isPresent()) {
                ScheduleClass schedule = relevantScheduleOpt.get();
                System.out.println("DEBUG: Relevant schedule found. Start time: " + schedule.getStartTime());
                // Check for lateness against the found schedule's start time.
                if (attendanceRecord.getEntryDateTime().isAfter(schedule.getStartTime())) {
                    attendanceRecord.setStatus(AttendanceStatus.LATE);
                    System.out.println("DEBUG: Status set to LATE.");
                } else {
                    attendanceRecord.setStatus(AttendanceStatus.PRESENT);
                    System.out.println("DEBUG: Status set to PRESENT.");
                }
            } else {
                System.out.println("DEBUG: No relevant schedule found. Defaulting status.");
                // If no schedule started before the entry time (e.g., employee came in super early
                // or on a day off), default to PRESENT to avoid null status.
                attendanceRecord.setStatus(AttendanceStatus.PRESENT);
                System.out.println("DEBUG: Status set to PRESENT by default.");
            }
        }

        System.out.println("DEBUG: Before saving, status is: " + attendanceRecord.getStatus());
        return attendanceRepositoryPort.save(attendanceRecord);
    }

    @Override
    public List<AttendanceRecordClass> findEmployeeAttendances(Long employeeId, LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + employeeId));
        return attendanceRepositoryPort.findByEmployee(employee).stream()
                .filter(record -> record.getEntryDateTime() != null && record.getEntryDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public double calculateAttendancePercentage(Long employeeId, int year, int month, int day) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, startOfMonth.lengthOfMonth());

        List<AttendanceRecordClass> records = getAttendanceListByEmployeeAndDateRange(employeeId, startOfMonth, endOfMonth);

        long daysWithAttendance = records.stream()
                .filter(record -> record.getEntryDateTime() != null)
                .map(record -> record.getEntryDateTime().toLocalDate())
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Override
    public AttendanceRecordClass updateAttendance(AttendanceRecordClass attendanceRecord) {
        if (attendanceRecord.getId() == null) {
            throw new IllegalArgumentException("Attendance record ID cannot be null for update.");
        }

        AttendanceRecordClass existingRecord = attendanceRepositoryPort.findById(attendanceRecord.getId())
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance record not found: " + attendanceRecord.getId()));

        if (attendanceRecord.getEntryDateTime() != null) {
            existingRecord.setEntryDateTime(attendanceRecord.getEntryDateTime());
        }
        if (attendanceRecord.getExitDateTime() != null) {
            existingRecord.setExitDateTime(attendanceRecord.getExitDateTime());
        }
        if (attendanceRecord.getStatus() != null) {
            existingRecord.setStatus(attendanceRecord.getStatus());
        }

        if (existingRecord.getExitDateTime() != null &&
                existingRecord.getEntryDateTime() != null &&
                existingRecord.getExitDateTime().isBefore(existingRecord.getEntryDateTime())) {
            throw new InvalidAttendanceDataException("Exit time cannot be before entry time.");
        }

        return attendanceRepositoryPort.save(existingRecord);
    }

    @Override
    public void deleteById(Long attendanceId) {
        if (!attendanceRepositoryPort.findById(attendanceId).isPresent()) {
            throw new AttendanceNotFoundException("Attendance record not found: " + attendanceId);
        }
        attendanceRepositoryPort.deleteById(attendanceId);
    }
}
