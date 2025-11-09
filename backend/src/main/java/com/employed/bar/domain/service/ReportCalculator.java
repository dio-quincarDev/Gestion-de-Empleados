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
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReportCalculator {

    public HoursCalculation calculateHours(List<AttendanceRecordClass> records, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalHours = records.stream()
                .map(record -> {
                    if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
                        LocalDateTime effectiveEntry = record.getEntryDateTime().isBefore(startDate) ? startDate : record.getEntryDateTime();
                        LocalDateTime effectiveExit = record.getExitDateTime().isAfter(endDate) ? endDate : record.getExitDateTime();

                        if (effectiveEntry.isAfter(effectiveExit)) {
                            return BigDecimal.ZERO;
                        }

                        return BigDecimal.valueOf(Duration.between(effectiveEntry, effectiveExit).toMinutes())
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

    public AttendanceReportLine mapToAttendanceReportLine(AttendanceRecordClass record, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal workedHours = BigDecimal.ZERO;
        if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
            LocalDateTime effectiveEntry = record.getEntryDateTime().isBefore(startDate) ? startDate : record.getEntryDateTime();
            LocalDateTime effectiveExit = record.getExitDateTime().isAfter(endDate) ? endDate : record.getExitDateTime();

            if (effectiveEntry.isBefore(effectiveExit)) {
                workedHours = BigDecimal.valueOf(Duration.between(effectiveEntry, effectiveExit).toMinutes())
                        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            }
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
