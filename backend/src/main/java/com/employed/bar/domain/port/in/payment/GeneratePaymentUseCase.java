package com.employed.bar.domain.port.in.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface GeneratePaymentUseCase {
    BigDecimal generatePayment(Long employeeId, LocalDate startDate, LocalDate endDate);
}
