package com.employed.bar.domain.model.structure;

import com.employed.bar.domain.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordClass {
    private Long id;
    private EmployeeClass employee;
    private LocalDateTime entryDateTime;
    private LocalDateTime exitDateTime;
    private AttendanceStatus status;


}
