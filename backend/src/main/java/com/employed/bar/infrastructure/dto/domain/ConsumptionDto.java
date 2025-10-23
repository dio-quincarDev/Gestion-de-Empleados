package com.employed.bar.infrastructure.dto.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for consumption records.
 * Used to transfer consumption data between the API layer and the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionDto {
    private Long id;

    @NotNull(message = "El ID del empleado no puede ser nulo")
    private Long employeeId;

    @NotNull(message = "La fecha de consumo no puede ser nula")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String description;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;
}