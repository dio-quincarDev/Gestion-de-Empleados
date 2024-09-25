package com.employed.bar.adapters.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ConsumptionReportDto {
    String employeeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime consumptionDate;
    BigDecimal amount;
}