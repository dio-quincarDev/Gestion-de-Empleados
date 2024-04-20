package com.employed.bar.adapters.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long employeeId;
}
