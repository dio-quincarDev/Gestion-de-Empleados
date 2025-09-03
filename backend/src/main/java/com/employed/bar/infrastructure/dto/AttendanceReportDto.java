package com.employed.bar.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AttendanceReportDto {
    private String employeeName;

    @JsonAlias("date")
    private LocalDate attendanceDate;
    @JsonAlias("startDate")
    private LocalTime entryTime;
    @JsonAlias("endDate")
    private LocalTime exitTime;

    private double workedHours;

    public double attendancePercentage; // Campo para almacenar las horas trabajadas, sin lógica de cálculo
}
