package com.employed.bar.application.service;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.OvertimeSuggestion;
import com.employed.bar.domain.port.in.service.OvertimeSuggestionUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OvertimeSuggestionApplicationService implements OvertimeSuggestionUseCase {

    private final EmployeeRepositoryPort employeeRepositoryPort;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private static final long EIGHT_HOURS_IN_MINUTES = 480;

    public OvertimeSuggestionApplicationService(EmployeeRepositoryPort employeeRepositoryPort, AttendanceRepositoryPort attendanceRepositoryPort) {
        this.employeeRepositoryPort = employeeRepositoryPort;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
    }

    @Override
    public List<OvertimeSuggestion> generateSuggestions() {
        List<Employee> employeesWithoutOvertimePay = employeeRepositoryPort.findAll().stream()
                .filter(employee -> !employee.isPaysOvertime())
                .collect(Collectors.toList());

        List<OvertimeSuggestion> suggestions = new ArrayList<>();

        for (Employee employee : employeesWithoutOvertimePay) {
            List<AttendanceRecord> records = attendanceRepositoryPort.findByEmployee(employee);

            // Group records by day using the 'date' field
            Map<LocalDate, List<AttendanceRecord>> recordsByDay = records.stream()
                    .filter(record -> record.getDate() != null)
                    .collect(Collectors.groupingBy(AttendanceRecord::getDate));

            for (Map.Entry<LocalDate, List<AttendanceRecord>> entry : recordsByDay.entrySet()) {
                LocalDate day = entry.getKey();
                List<AttendanceRecord> dayRecords = entry.getValue();

                long totalMinutesWorked = dayRecords.stream()
                        .filter(record -> record.getEntryTime() != null && record.getExitTime() != null)
                        .mapToLong(record -> {
                            LocalDateTime entryDateTime = day.atTime(record.getEntryTime());
                            LocalDateTime exitDateTime = day.atTime(record.getExitTime());
                            return Duration.between(entryDateTime, exitDateTime).toMinutes();
                        })
                        .sum();

                if (totalMinutesWorked > EIGHT_HOURS_IN_MINUTES) {
                    long extraMinutes = totalMinutesWorked - EIGHT_HOURS_IN_MINUTES;
                    suggestions.add(new OvertimeSuggestion(employee, day, extraMinutes));
                }
            }
        }

        return suggestions;
    }
}
