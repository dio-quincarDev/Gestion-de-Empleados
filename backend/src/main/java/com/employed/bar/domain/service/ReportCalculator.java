package com.employed.bar.domain.service;

import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class ReportCalculator {

    public HoursCalculation calculateHours(List<AttendanceRecordClass> records) {
        double totalHours = records.stream()
                .mapToDouble(record -> {
                    if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
                        // Duration.between handles overnight shifts correctly with LocalDateTime
                        return Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes() / 60.0;
                    }
                    return 0;
                })
                .sum();

        // TODO: Implement overtime calculation logic
        double regularHours = totalHours;
        double overtimeHours = 0;

        return new HoursCalculation(totalHours, regularHours, overtimeHours);
    }

    public AttendanceReportLine mapToAttendanceReportLine(AttendanceRecordClass record) {
        double workedHours = 0;
        if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
            workedHours = Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes() / 60.0;
        }
        return new AttendanceReportLine(
                record.getEmployee().getName(),
                record.getEntryDateTime(),
                record.getExitDateTime(),
                workedHours,
                100.0 // Placeholder for percentage
        );
    }

    public ConsumptionReportLine mapToConsumptionReportLine(ConsumptionClass consumption) {
        return new ConsumptionReportLine(
                consumption.getEmployee().getName(),
                consumption.getConsumptionDate(),
                consumption.getAmount(),
                consumption.getDescription()
        );
    }
}
