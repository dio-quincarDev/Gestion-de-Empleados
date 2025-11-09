package com.employed.bar.application.service;

import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.domain.port.in.payment.GeneratePaymentUseCase;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.service.ReportCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GeneratePaymentApplicationService implements GeneratePaymentUseCase {

    private final AttendanceUseCase attendanceUseCase;
    private final EmployeeUseCase employeeUseCase;
    private final PaymentCalculationUseCase paymentCalculationUseCase;
    private final ReportCalculator reportCalculator;

    public GeneratePaymentApplicationService(AttendanceUseCase attendanceUseCase, EmployeeUseCase employeeUseCase, PaymentCalculationUseCase paymentCalculationUseCase, ReportCalculator reportCalculator) {
        this.attendanceUseCase = attendanceUseCase;
        this.employeeUseCase = employeeUseCase;
        this.paymentCalculationUseCase = paymentCalculationUseCase;
        this.reportCalculator = reportCalculator;
    }


    @Override
    public BigDecimal generatePayment(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeUseCase.getEmployeeById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        List<AttendanceRecordClass> attendanceRecords = attendanceUseCase.getAttendanceListByEmployeeAndDateRange(employeeId, startDate, endDate);

        HoursCalculation hoursCalculation = reportCalculator.calculateHours(attendanceRecords, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        return paymentCalculationUseCase.calculateTotalPay(
                employee.getPaymentType(),
                employee.getSalary(),
                employee.getHourlyRate(),
                employee.isPaysOvertime(),
                employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(),
                hoursCalculation.getOvertimeHours()
        );
    }
}
