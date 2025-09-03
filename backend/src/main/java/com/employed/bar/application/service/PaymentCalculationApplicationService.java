package com.employed.bar.application.service;

import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentCalculationApplicationService implements PaymentCalculationUseCase {

    @Override
    public BigDecimal calculateTotalPay(BigDecimal hourlyRate, boolean paysOvertime, OvertimeRateType overtimeRateType, double regularHours, double overtimeHours) {
        BigDecimal regularPay = hourlyRate.multiply(BigDecimal.valueOf(regularHours));
        BigDecimal totalOvertimePay = BigDecimal.ZERO;

        if (paysOvertime && overtimeHours > 0) {
            BigDecimal overtimeRateMultiplier = BigDecimal.ONE; // Base rate for overtime hours

            if (overtimeRateType != null) { // Ensure overtimeRateType is not null
                overtimeRateMultiplier = overtimeRateMultiplier.add(overtimeRateType.getMultiplier());
            } else {
                // Default to 100% extra if paysOvertime is true but type is not specified
                overtimeRateMultiplier = overtimeRateMultiplier.add(OvertimeRateType.ONE_HUNDRED_PERCENT.getMultiplier());
            }

            totalOvertimePay = hourlyRate.multiply(overtimeRateMultiplier).multiply(BigDecimal.valueOf(overtimeHours));
        }

        return regularPay.add(totalOvertimePay).setScale(2, RoundingMode.HALF_UP);
    }
}
