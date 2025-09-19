package com.employed.bar.domain.model.report.hours;

import lombok.Value;

@Value
public class HoursCalculation {
    double totalHours;
    double regularHours;
    double overtimeHours;
}
