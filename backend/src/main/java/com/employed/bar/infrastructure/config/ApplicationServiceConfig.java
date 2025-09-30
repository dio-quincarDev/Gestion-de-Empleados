package com.employed.bar.infrastructure.config;

import com.employed.bar.application.service.*;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.*;
import com.employed.bar.domain.service.ManagerReportCalculator;
import com.employed.bar.domain.service.ReportCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableTransactionManagement
public class ApplicationServiceConfig {

    @Bean
    @Transactional
    public AttendanceApplicationService attendanceApplicationService(
            EmployeeRepositoryPort employeeRepository,
            AttendanceRepositoryPort attendanceRepositoryPort) {
        return new AttendanceApplicationService(employeeRepository, attendanceRepositoryPort);
    }

    @Bean
    @Transactional
    public ConsumptionApplicationService consumptionApplicationService(
            EmployeeRepositoryPort employeeRepository,
            ConsumptionRepositoryPort consumptionRepositoryPort) {
        return new ConsumptionApplicationService(employeeRepository, consumptionRepositoryPort);
    }

    @Bean
    @Transactional
    public EmployeeApplicationService employeeApplicationService(
            EmployeeRepositoryPort employeeRepositoryPort,
            AttendanceUseCase attendanceUseCase,
            PaymentCalculationUseCase paymentCalculationUseCase) {
        return new EmployeeApplicationService(employeeRepositoryPort, attendanceUseCase, paymentCalculationUseCase);
    }

    @Bean
    public GeneratePaymentApplicationService generatePaymentApplicationService(
            AttendanceUseCase attendanceUseCase,
            EmployeeUseCase employeeUseCase,
            PaymentCalculationUseCase paymentCalculationUseCase) {
        return new GeneratePaymentApplicationService(attendanceUseCase, employeeUseCase, paymentCalculationUseCase);
    }

    @Bean
    public KpiApplicationService kpiApplicationService(
            EmployeeRepositoryPort employeeRepository,
            AttendanceRepositoryPort attendanceRepository,
            ConsumptionRepositoryPort consumptionRepositoryPort) {
        return new KpiApplicationService(employeeRepository, attendanceRepository, consumptionRepositoryPort);
    }

    @Bean
    public ManagerReportApplicationService managerReportApplicationService(
            EmployeeRepositoryPort employeeRepository,
            ReportingUseCase reportingUseCase,
            ManagerReportCalculator managerReportCalculator,
            NotificationPort notificationPort,
            PdfGeneratorPort pdfGeneratorPort) {
        return new ManagerReportApplicationService(employeeRepository, reportingUseCase, managerReportCalculator, notificationPort, pdfGeneratorPort);
    }

    @Bean
    public OvertimeSuggestionApplicationService overtimeSuggestionApplicationService(
            EmployeeRepositoryPort employeeRepositoryPort,
            AttendanceRepositoryPort attendanceRepositoryPort) {
        return new OvertimeSuggestionApplicationService(employeeRepositoryPort, attendanceRepositoryPort);
    }

    @Bean
    public PaymentCalculationApplicationService paymentCalculationApplicationService() {
        return new PaymentCalculationApplicationService();
    }

    @Bean
    public ReportingApplicationService reportingApplicationService(
            EmployeeRepositoryPort employeeRepository,
            ConsumptionRepositoryPort consumptionRepositoryPort,
            AttendanceRepositoryPort attendanceRepositoryPort,
            ReportCalculator reportCalculator,
            PaymentCalculationUseCase paymentCalculationUseCase,
            SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase) {
        return new ReportingApplicationService(employeeRepository, consumptionRepositoryPort, attendanceRepositoryPort, reportCalculator, paymentCalculationUseCase, sendEmployeeReportNotificationUseCase);
    }

    @Bean
    @Transactional
    public ScheduleApplicationService scheduleApplicationService(
            ScheduleRepositoryPort scheduleRepositoryPort,
            EmployeeRepositoryPort employeeRepository) {
        return new ScheduleApplicationService(scheduleRepositoryPort, employeeRepository);
    }
}
