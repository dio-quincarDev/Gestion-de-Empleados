package com.employed.bar.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
        private Long id;
    private String name;
    private String email;
    private String role;
    private BigDecimal salary;
    private String status;
    private List<Schedule> schedules = new ArrayList<>();
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private List<Consumption> consumptions = new ArrayList<>();
}
