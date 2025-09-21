package com.employed.bar.infrastructure.config;



import com.employed.bar.application.service.ReportingApplicationService;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.service.ReportCalculator;

import com.employed.bar.application.service.PaymentCalculationApplicationService;

import com.employed.bar.application.service.OvertimeSuggestionApplicationService;

import com.employed.bar.application.service.ManagerReportApplicationService;
import com.employed.bar.domain.port.out.NotificationPort;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.employed.bar.domain.service.ManagerReportCalculator;

import com.employed.bar.application.service.KpiApplicationService;

import com.employed.bar.application.service.GeneratePaymentApplicationService;
import com.employed.bar.domain.port.in.service.EmployeeUseCase;

import com.employed.bar.application.service.EmployeeApplicationService;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;

import com.employed.bar.application.service.ConsumptionApplicationService;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
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
            AttendanceRepositoryPort attendanceRepositoryPort,
            ReportingUseCase reportingUseCase) {
        return new AttendanceApplicationService(employeeRepository, attendanceRepositoryPort, reportingUseCase);
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
    @Transactional
    public ReportingApplicationService reportingApplicationService(
            EmployeeRepositoryPort employeeRepository,
            ConsumptionRepositoryPort consumptionRepositoryPort,
            AttendanceRepositoryPort attendanceRepositoryPort,
            ReportCalculator reportCalculator,
            PaymentCalculationUseCase paymentCalculationUseCase,
            SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase) {
        return new ReportingApplicationService(employeeRepository, consumptionRepositoryPort, attendanceRepositoryPort, reportCalculator, paymentCalculationUseCase, sendEmployeeReportNotificationUseCase);
    }
}
