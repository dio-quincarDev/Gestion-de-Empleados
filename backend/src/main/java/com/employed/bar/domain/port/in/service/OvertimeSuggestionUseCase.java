package com.employed.bar.domain.port.in.service;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeSuggestionUseCase {
    List<LocalDate> getOvertimeSuggestions(Long employeeId, LocalDate startDate, LocalDate endDate);
}
