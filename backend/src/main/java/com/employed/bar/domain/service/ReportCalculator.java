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
        double totalHoursDouble = records.stream()
                .mapToDouble(record -> {
                    if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
                        return Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes() / 60.0;
                    }
                    return 0;
                })
                .sum();

        BigDecimal totalHours = BigDecimal.valueOf(totalHoursDouble).setScale(2, RoundingMode.HALF_UP);

        // TODO: Implement overtime calculation logic
        BigDecimal regularHours = totalHours;
        BigDecimal overtimeHours = BigDecimal.ZERO;

        return new HoursCalculation(totalHours.doubleValue(), regularHours.doubleValue(), overtimeHours.doubleValue());
    }

    public AttendanceReportLine mapToAttendanceReportLine(AttendanceRecordClass record) {
        double workedHoursDouble = 0;
        if (record.getEntryDateTime() != null && record.getExitDateTime() != null) {
            workedHoursDouble = Duration.between(record.getEntryDateTime(), record.getExitDateTime()).toMinutes() / 60.0;
        }
        BigDecimal workedHours = BigDecimal.valueOf(workedHoursDouble).setScale(2, RoundingMode.HALF_UP);

        return new AttendanceReportLine(
                record.getEmployee().getName(),
                record.getEntryDateTime(),
                record.getExitDateTime(),
                workedHours.doubleValue(),
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
