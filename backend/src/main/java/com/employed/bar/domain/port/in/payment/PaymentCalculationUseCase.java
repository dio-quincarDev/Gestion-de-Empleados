package com.employed.bar.domain.port.in.payment;

import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;

import java.math.BigDecimal;

public interface PaymentCalculationUseCase {

    BigDecimal calculateTotalPay(PaymentType paymentType,

                                 BigDecimal salary,

                                 BigDecimal hourlyRate,

                                 boolean paysOvertime,

                                 OvertimeRateType overtimeRateType,

                                 double regularHours,

                                 double overtimeHours);

    BigDecimal calculateTotalPay(BigDecimal hourlyRate, boolean paysOvertime, OvertimeRateType overtimeRateType, double totalHours, int i);
}
