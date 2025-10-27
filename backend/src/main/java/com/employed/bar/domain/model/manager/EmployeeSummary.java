package com.employed.bar.domain.model.manager;

import com.employed.bar.domain.model.payment.PaymentMethod;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class EmployeeSummary {
    String employeeName;
    BigDecimal totalHoursWorked;
    BigDecimal totalEarnings;
    BigDecimal totalConsumptions;
    BigDecimal netPay;
    PaymentMethod paymentMethod;
}
