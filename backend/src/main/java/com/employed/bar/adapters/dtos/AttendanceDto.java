package com.employed.bar.adapters.dtos;

import com.employed.bar.domain.model.Employee;
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
public class AttendanceDto {
  private Long employeeId;
  private Employee employee;
  private LocalDate date;
  private LocalTime entryTime;
  private LocalTime exitTime;
  private String status;
}
