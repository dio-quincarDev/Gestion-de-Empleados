package com.employed.bar.service;

import com.employed.bar.application.service.PaymentCalculationApplicationService;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PaymentCalculationApplicationServiceTest {

    @InjectMocks
    private PaymentCalculationApplicationService paymentCalculationService;

    @Test
    void testCalculateTotalPay_NoOvertime() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 8.0;
        double overtimeHours = 0.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, false, null, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("80.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_WithOvertime_ButOvertimeNotPaid() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 8.0;
        double overtimeHours = 2.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, false, null, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("80.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_WithOvertime_PaidAtOneHundredPercent() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 8.0;
        double overtimeHours = 2.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, true, OvertimeRateType.ONE_HUNDRED_PERCENT, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("120.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_WithOvertime_PaidAtFiftyPercent() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 8.0;
        double overtimeHours = 2.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, true, OvertimeRateType.FIFTY_PERCENT, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("110.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_ZeroHours() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 0.0;
        double overtimeHours = 0.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, true, OvertimeRateType.ONE_HUNDRED_PERCENT, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_NullHourlyRate() {
        assertThrows(NullPointerException.class, () -> {
            paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, null, false, null, 8.0, 0.0);
        });
    }

    @Test
    void testCalculateTotalPay_NullOvertimeRateType_PaysOvertime() {
        // Given
        BigDecimal hourlyRate = new BigDecimal("10.00");
        double regularHours = 8.0;
        double overtimeHours = 2.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.HOURLY, BigDecimal.ZERO, hourlyRate, true, null, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }

    @Test
    void testCalculateTotalPay_SalariedEmployee_WithOvertime() {
        // Given
        BigDecimal salary = new BigDecimal("2000.00");
        BigDecimal hourlyRate = new BigDecimal("20.00");
        double regularHours = 40.0; // Not used for salaried employees
        double overtimeHours = 5.0;

        // When
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(PaymentType.SALARIED, salary, hourlyRate, true, OvertimeRateType.FIFTY_PERCENT, regularHours, overtimeHours);

        // Then
        // Salary: 2000
        // Overtime pay: 5 * 20 * 1.5 = 150
        // Total: 2150
        BigDecimal expectedPay = new BigDecimal("2150.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }
}
