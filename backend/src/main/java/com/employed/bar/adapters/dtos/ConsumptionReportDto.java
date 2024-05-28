package com.employed.bar.adapters.dtos;

import com.employed.bar.domain.model.Consumption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionReportDto {
    private String employeeName;
    private LocalDate consumptionDate;
    private String description;
    private BigDecimal amount;

    private List<Consumption> consumptions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ConsumptionReportDto(List<Consumption> consumptions, LocalDateTime startDate, LocalDateTime endDate) {
        this.consumptions = consumptions;
        this.startDate = startDate;
        this.endDate = endDate;

    }
}
