package com.employed.bar.infrastructure.dto.domain;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for attendance records.
 * Used to transfer attendance data between the API layer and the application layer.
 */
@Data
public class AttendanceDto {

  @JsonAlias({"employee_id", "employeeId"})
  private Long employeeId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonAlias({"entry_date_time", "entryDateTime"})
  private LocalDateTime entryDateTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonAlias({"exit_date_time", "exitDateTime"})
  private LocalDateTime exitDateTime;

  @JsonAlias({"status", "attendanceStatus"})
  private AttendanceStatus status;
}
