package com.employed.bar.domain.service;

import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Component
public class ReportCalculator {

    public HoursCalculation calculateHours(List<AttendanceRecordClass> records) {
        BigDecimal totalHours = records.stream()
                .map(record -> {
                    if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
                        return BigDecimal.valueOf(Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes())
                                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TODO: Implement overtime calculation logic
        BigDecimal regularHours = totalHours;
        BigDecimal overtimeHours = BigDecimal.ZERO;

        return new HoursCalculation(totalHours, regularHours, overtimeHours);
    }

    public AttendanceReportLine mapToAttendanceReportLine(AttendanceRecordClass record) {
        BigDecimal workedHours = BigDecimal.ZERO;
        if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
            workedHours = BigDecimal.valueOf(Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes())
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
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
