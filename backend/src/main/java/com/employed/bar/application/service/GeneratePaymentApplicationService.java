package com.employed.bar.application.service;

import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.EmployeeUseCase;
import com.employed.bar.domain.port.in.service.GeneratePaymentUseCase;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratePaymentApplicationService implements GeneratePaymentUseCase {

    private final AttendanceUseCase attendanceUseCase;
    private final EmployeeUseCase employeeUseCase;
    private final PaymentCalculationUseCase paymentCalculationUseCase;

    @Override
    public BigDecimal generatePayment(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeUseCase.getEmployeeById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        List<AttendanceRecordClass> attendanceRecords = attendanceUseCase.getAttendanceListByEmployeeAndDateRange(employeeId, startDate, endDate);

        double totalHours = attendanceRecords.stream()
                .mapToDouble(record -> {
                    if (record.getEntryTime() != null && record.getExitTime() != null) {
                        return Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
                    }
                    return 0.0;
                })
                .sum();

        return paymentCalculationUseCase.calculateTotalPay(
                employee.getHourlyRate(),
                employee.isPaysOvertime(),
                employee.getOvertimeRateType(),
                totalHours,
                0 // We pass 0 for overtimeHours as the new logic calculates based on total hours
        );
    }
}
