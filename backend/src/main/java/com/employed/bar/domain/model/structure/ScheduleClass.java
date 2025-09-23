package com.employed.bar.domain.model.structure;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleClass {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EmployeeClass employee;


}
