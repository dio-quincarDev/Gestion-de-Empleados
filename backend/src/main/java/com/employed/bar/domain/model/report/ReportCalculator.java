package com.employed.bar.domain.model.report;

import com.employed.bar.domain.model.report.hours.HoursCalculation;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ReportCalculator {

    public HoursCalculation calculateHours(List<AttendanceRecordClass> records) {
        double totalHours = records.stream()
                .mapToDouble(record -> {
                    if (record.getEntryTime() != null && record.getExitTime() != null) {
                        return java.time.Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
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
        if (record.getEntryTime() != null && record.getExitTime() != null) {
            workedHours = java.time.Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
        }
        return new AttendanceReportLine(
                record.getEmployee().getName(),
                record.getDate(),
                record.getEntryTime(),
                record.getExitTime(),
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
