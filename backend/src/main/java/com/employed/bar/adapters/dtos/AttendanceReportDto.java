package com.employed.bar.adapters.dtos;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    private double attendancePercentage;

    public AttendanceReportDto(String employeeName, LocalDate attendanceDate) {
        this.employeeName = employeeName;
        this.attendanceDate = attendanceDate;
    }

    public AttendanceReportDto(Employee employee, List<AttendanceRecord> records) {
        this.employeeName = employee.getName();
        if (!records.isEmpty()) {
            this.attendanceDate = records.get(0).getDate();
            this.entryTime = records.get(0).getEntryTime();
            this.exitTime = records.get(records.size() - 1).getExitTime();
            this.attendancePercentage = calculateAttendancePercentage(records);
        }else {
            this.attendanceDate = null;
            this.entryTime = null;
            this.exitTime = null;
            this.attendancePercentage = 0.0;
        }
    }

    // Método para calcular el porcentaje de asistencia
    private double calculateAttendancePercentage(List<AttendanceRecord> attendanceRecords) {
        long totalDays = attendanceRecords.size();  // Total de días registrados
        long presentDays = attendanceRecords.stream()
                .filter(record -> "present".equalsIgnoreCase(record.getStatus())) // Filtrar los días presentes
                .count();

        if (totalDays == 0) {
            return 0.0; // Evitar división por cero
        }
        return (double) presentDays / totalDays * 100; // Calcular porcentaje
    }
}