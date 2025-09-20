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

        BigDecimal overtimePay = BigDecimal.ZERO;
        if (paysOvertime && overtimeHours > 0) {
            BigDecimal overtimeRate;
            if (overtimeRateType == OvertimeRateType.ONE_HUNDRED_PERCENT) {
                overtimeRate = hourlyRate.multiply(BigDecimal.valueOf(2));
            } else if (overtimeRateType == OvertimeRateType.FIFTY_PERCENT) {
                overtimeRate = hourlyRate.multiply(BigDecimal.valueOf(1.5));
            } else {
                overtimeRate = hourlyRate; // Default to regular rate if type is null or other
            }
            overtimePay = overtimeRate.multiply(BigDecimal.valueOf(overtimeHours));
        }

        return regularPay.add(overtimePay).setScale(2, RoundingMode.HALF_UP);
    }
}
