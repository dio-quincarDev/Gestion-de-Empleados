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
        double totalHours = regularHours + overtimeHours;
        return hourlyRate.multiply(BigDecimal.valueOf(totalHours)).setScale(2, RoundingMode.HALF_UP);
    }
}
