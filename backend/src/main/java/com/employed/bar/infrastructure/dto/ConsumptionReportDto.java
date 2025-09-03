package com.employed.bar.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Data
public class ConsumptionReportDto {
    String employeeName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime consumptionDate;

    BigDecimal amount;

    String description;
}