package com.employed.bar.infrastructure.dto.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceDto {

  @JsonAlias({"employee_id", "employeeId"})
  private Long employeeId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonAlias({"date", "attendanceDate"})
  private LocalDate date;

  @JsonFormat(pattern = "HH:mm")
  @JsonAlias({"entry_time", "entryTime"})
  private LocalTime entryTime;

  @JsonFormat(pattern = "HH:mm")
  @JsonAlias({"exit_time", "exitTime"})
  private LocalTime exitTime;

  @JsonAlias({"status", "attendanceStatus"})
  private String status;
}
