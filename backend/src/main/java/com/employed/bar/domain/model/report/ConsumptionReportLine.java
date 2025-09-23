package com.employed.bar.domain.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionReportLine {
    private String employeeName;
    private LocalDateTime consumptionDate;
    private BigDecimal amount;
    private String description;
}
