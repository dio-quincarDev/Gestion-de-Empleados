package com.employed.bar.domain.model.payment;

import com.employed.bar.domain.model.structure.EmployeeClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class OvertimeSuggestion {
    private final EmployeeClass employee;
    private final LocalDate date;
    private final long extraMinutesWorked;
}
