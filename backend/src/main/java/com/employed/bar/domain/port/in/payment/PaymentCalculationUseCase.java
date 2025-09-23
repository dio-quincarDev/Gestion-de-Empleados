package com.employed.bar.domain.port.in.payment;

import com.employed.bar.domain.enums.OvertimeRateType;
import java.math.BigDecimal;

public interface PaymentCalculationUseCase {
    BigDecimal calculateTotalPay(BigDecimal hourlyRate,
                                 boolean paysOvertime,
                                 OvertimeRateType overtimeRateType,
                                 double regularHours,
                                 double overtimeHours);
}
