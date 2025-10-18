package com.employed.bar.infrastructure.dto.domain;

import com.employed.bar.domain.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for attendance records.
 * Used to transfer attendance data between the API layer and the application layer.
 */
@Data
public class AttendanceDto {

  @NotNull(message = "Employee ID cannot be null")
  @JsonAlias({"employee_id", "employeeId"})
  private Long employeeId;

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonAlias({"entry_date_time", "entryDateTime"})
  private LocalDateTime entryDateTime;

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonAlias({"exit_date_time", "exitDateTime"})
  private LocalDateTime exitDateTime;

  @JsonAlias({"status", "attendanceStatus"})
  private AttendanceStatus status;
}
