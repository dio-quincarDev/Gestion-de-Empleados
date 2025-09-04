package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.OvertimeSuggestion;

import java.util.List;

public interface OvertimeSuggestionUseCase {
    List<OvertimeSuggestion> generateSuggestions();
}