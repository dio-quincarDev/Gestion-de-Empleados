package com.employed.bar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class OvertimeSuggestion {
    private final Employee employee;
    private final LocalDate date;
    private final long extraMinutesWorked;
}
