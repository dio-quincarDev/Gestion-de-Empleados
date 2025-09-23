package com.employed.bar.domain.model.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordClass {
    private Long id;
    private EmployeeClass employee;
    private LocalDate date;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private String status;


}
