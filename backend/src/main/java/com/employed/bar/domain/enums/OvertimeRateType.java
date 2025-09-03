package com.employed.bar.domain.enums;

import java.math.BigDecimal;

public enum OvertimeRateType {
    FIFTY_PERCENT(BigDecimal.valueOf(0.5)),
    ONE_HUNDRED_PERCENT(BigDecimal.valueOf(1.0));

    private final BigDecimal multiplier;

    OvertimeRateType(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }
}
