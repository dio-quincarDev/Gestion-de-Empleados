package com.employed.bar.application.service;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.payment.OvertimeSuggestion;
import com.employed.bar.domain.port.in.payment.OvertimeSuggestionUseCase;
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
        List<EmployeeClass> employeesWithoutOvertimePay = employeeRepositoryPort.findAll().stream()
                .filter(employee -> !employee.isPaysOvertime())
                .collect(Collectors.toList());

        List<OvertimeSuggestion> suggestions = new ArrayList<>();

        for (EmployeeClass employee : employeesWithoutOvertimePay) {
            List<AttendanceRecordClass> records = attendanceRepositoryPort.findByEmployee(employee);

            // Group records by day using the 'date' field
            Map<LocalDate, List<AttendanceRecordClass>> recordsByDay = records.stream()
                    .filter(record -> record.getDate() != null)
                    .collect(Collectors.groupingBy(AttendanceRecordClass::getDate));

            for (Map.Entry<LocalDate, List<AttendanceRecordClass>> entry : recordsByDay.entrySet()) {
                LocalDate day = entry.getKey();
                List<AttendanceRecordClass> dayRecords = entry.getValue();

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
