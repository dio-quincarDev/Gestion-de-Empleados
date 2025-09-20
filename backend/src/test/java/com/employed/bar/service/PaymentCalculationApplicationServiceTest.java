package com.employed.bar.service;

import com.employed.bar.application.service.PaymentCalculationApplicationService;
import com.employed.bar.domain.enums.OvertimeRateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(hourlyRate, false, null, regularHours, overtimeHours);

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
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(hourlyRate, false, null, regularHours, overtimeHours);

        // Then
        // This test will fail with the current implementation, as it simply adds all hours.
        // The correct behavior should be to only pay for regular hours.
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
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(hourlyRate, true, OvertimeRateType.ONE_HUNDRED_PERCENT, regularHours, overtimeHours);

        // Then
        // Regular pay: 8 * 10 = 80
        // Overtime pay: 2 * 10 * 2 = 40
        // Total: 120
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
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(hourlyRate, true, OvertimeRateType.FIFTY_PERCENT, regularHours, overtimeHours);

        // Then
        // Regular pay: 8 * 10 = 80
        // Overtime pay: 2 * 10 * 1.5 = 30
        // Total: 110
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
        BigDecimal totalPay = paymentCalculationService.calculateTotalPay(hourlyRate, true, OvertimeRateType.ONE_HUNDRED_PERCENT, regularHours, overtimeHours);

        // Then
        BigDecimal expectedPay = new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPay, totalPay);
    }
}
