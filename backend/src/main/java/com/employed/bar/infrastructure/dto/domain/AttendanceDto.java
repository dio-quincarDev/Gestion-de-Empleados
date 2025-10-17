package com.employed.bar.infrastructure.dto.domain;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for attendance records.
 * Used to transfer attendance data between the API layer and the application layer.
 */
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
  private AttendanceStatus status;
}
