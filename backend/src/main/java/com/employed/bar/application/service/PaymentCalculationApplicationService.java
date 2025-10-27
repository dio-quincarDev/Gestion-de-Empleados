package com.employed.bar.application.service;

import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentCalculationApplicationService implements PaymentCalculationUseCase {

    @Override
    public BigDecimal calculateTotalPay(PaymentType paymentType, BigDecimal salary, BigDecimal hourlyRate, boolean paysOvertime, OvertimeRateType overtimeRateType, BigDecimal regularHours, BigDecimal overtimeHours) {
        if (paymentType == PaymentType.SALARIED) {
            BigDecimal overtimePay = BigDecimal.ZERO;
            if (paysOvertime && overtimeHours.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal overtimeRate;
                if (overtimeRateType == OvertimeRateType.ONE_HUNDRED_PERCENT) {
                    overtimeRate = hourlyRate.multiply(new BigDecimal("2"));
                } else if (overtimeRateType == OvertimeRateType.FIFTY_PERCENT) {
                    overtimeRate = hourlyRate.multiply(new BigDecimal("1.5"));
                } else {
                    overtimeRate = hourlyRate; // Default to regular rate if type is null or other
                }
                overtimePay = overtimeRate.multiply(overtimeHours);
            }
            return salary.add(overtimePay).setScale(2, RoundingMode.HALF_UP);
        } else { // HOURLY
            BigDecimal regularPay = hourlyRate.multiply(regularHours);

            BigDecimal overtimePay = BigDecimal.ZERO;
            if (paysOvertime && overtimeHours.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal overtimeRate;
                if (overtimeRateType == OvertimeRateType.ONE_HUNDRED_PERCENT) {
                    overtimeRate = hourlyRate.multiply(new BigDecimal("2"));
                } else if (overtimeRateType == OvertimeRateType.FIFTY_PERCENT) {
                    overtimeRate = hourlyRate.multiply(new BigDecimal("1.5"));
                } else {
                    overtimeRate = hourlyRate; // Default to regular rate if type is null or other
                }
                overtimePay = overtimeRate.multiply(overtimeHours);
            }

            return regularPay.add(overtimePay).setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public BigDecimal calculateTotalPay(BigDecimal hourlyRate, boolean paysOvertime, OvertimeRateType overtimeRateType, BigDecimal totalHours, int i) {
        // Simplified calculation: assumes totalHours are regular hours and no overtime
        if (hourlyRate == null || totalHours == null) {
            return BigDecimal.ZERO;
        }
        return hourlyRate.multiply(totalHours).setScale(2, RoundingMode.HALF_UP);
    }
}
