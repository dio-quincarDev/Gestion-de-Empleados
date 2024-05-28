package com.employed.bar.adapters.dtos;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceReportDto {
    private String employeeName;
    private LocalDate date;
    private LocalTime entryTime;
    private LocalTime exitTime;

    private Employee employee;
    private List<AttendanceRecord> attendanceRecords;

    public AttendanceReportDto(Employee employee, List<AttendanceRecord> attendanceRecords) {
        this.employee = employee;
        this.attendanceRecords = attendanceRecords;
        this.employeeName = employee.getName();

    }
}
