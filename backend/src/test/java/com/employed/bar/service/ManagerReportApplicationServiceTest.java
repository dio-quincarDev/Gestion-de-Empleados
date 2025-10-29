package com.employed.bar.service;

import com.employed.bar.application.service.ManagerReportApplicationService;
import com.employed.bar.domain.enums.*;
import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.NotificationPort;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.employed.bar.domain.service.ManagerReportCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerReportApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;
    @Mock
    private ReportingUseCase reportingUseCase;
    @Mock
    private ManagerReportCalculator managerReportCalculator;
    @Mock
    private NotificationPort notificationPort;
    @Mock
    private PdfGeneratorPort pdfGeneratorPort;

    @InjectMocks
    private ManagerReportApplicationService managerReportApplicationService;

    private EmployeeClass employee;
    private Report individualReport;
    private ManagerReport managerReport;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);

        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Test Employee");

        individualReport = mock(Report.class);
        managerReport = mock(ManagerReport.class);
    }

    @Test
    void testGenerateAndSendManagerReport_Success() {
        // Arrange
        List<EmployeeClass> employees = Collections.singletonList(employee);
        List<Report> individualReports = Collections.singletonList(individualReport);
        String managerEmail = "manager@example.com";

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(employees));
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId())).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
        verify(reportingUseCase, times(1)).generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());
        verify(managerReportCalculator, times(1)).calculate(employees, individualReports);
        verify(notificationPort, times(1)).sendManagerReportByEmail(managerEmail, managerReport);
    }

    @Test
    void testGenerateAndSendManagerReport_NoEmployees() {
        // Arrange
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(managerReportCalculator.calculate(any(), any())).thenReturn(managerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
        verifyNoInteractions(reportingUseCase); // No reports should be generated
        verify(managerReportCalculator, times(1)).calculate(Collections.emptyList(), Collections.emptyList());
        verify(notificationPort, times(1)).sendManagerReportByEmail(anyString(), any(ManagerReport.class));
    }

    @Test
    void testGenerateManagerReportPdf_Success() {
        // Arrange
        List<EmployeeClass> employees = Collections.singletonList(employee);
        List<Report> individualReports = Collections.singletonList(individualReport);
        byte[] expectedPdf = {1, 2, 3, 4};

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(employees));
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId())).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);
        when(pdfGeneratorPort.generateManagerReportPdf(managerReport)).thenReturn(expectedPdf);

        // Act
        byte[] actualPdf = managerReportApplicationService.generateManagerReportPdf(startDate, endDate);

        // Assert
        assertNotNull(actualPdf);
        assertArrayEquals(expectedPdf, actualPdf);
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
        verify(reportingUseCase, times(1)).generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());
        verify(managerReportCalculator, times(1)).calculate(employees, individualReports);
        verify(pdfGeneratorPort, times(1)).generateManagerReportPdf(managerReport);
    }

    @Test
    void testGenerateManagerReportPdf_NoEmployees() {
        // Arrange
        byte[] expectedPdf = {1, 2, 3, 4};
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(managerReportCalculator.calculate(any(), any())).thenReturn(managerReport);
        when(pdfGeneratorPort.generateManagerReportPdf(managerReport)).thenReturn(expectedPdf);

        // Act
        byte[] actualPdf = managerReportApplicationService.generateManagerReportPdf(startDate, endDate);

        // Assert
        assertNotNull(actualPdf);
        assertArrayEquals(expectedPdf, actualPdf);
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
        verifyNoInteractions(reportingUseCase);
        verify(managerReportCalculator, times(1)).calculate(Collections.emptyList(), Collections.emptyList());
        verify(pdfGeneratorPort, times(1)).generateManagerReportPdf(managerReport);
    }

    @Test
    void testGenerateAndSendManagerReport_NullStartDate() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            managerReportApplicationService.generateAndSendManagerReport(null, endDate);
        });
        assertEquals("Start date and end date must not be null", thrown.getMessage());
        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, notificationPort);
    }

    @Test
    void testGenerateManagerReportPdf_NullEndDate() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            managerReportApplicationService.generateManagerReportPdf(startDate, null);
        });
        assertEquals("Start date and end date must not be null", thrown.getMessage());
        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, pdfGeneratorPort);
    }


    @Test
    void testGenerateAndSendManagerReport_VerifiesPaymentMethodContent() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);

        AchPaymentMethod achPaymentMethod = new AchPaymentMethod("Bank of America", "123456789", BankAccount.SAVINGS);
        EmployeeClass employee = new EmployeeClass(1L, "John Doe", "john.doe@example.com", "123-456-7890",
                EmployeeRole.BARTENDER, BigDecimal.valueOf(20), BigDecimal.valueOf(0), achPaymentMethod, true,
                OvertimeRateType.FIFTY_PERCENT, EmployeeStatus.ACTIVE, PaymentType.HOURLY,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        HoursCalculation hoursCalculation = new HoursCalculation(BigDecimal.valueOf(40), BigDecimal.valueOf(5), BigDecimal.valueOf(45));
        Report localIndividualReport = mock(Report.class);

        // Mock the behavior of localIndividualReport
        when(localIndividualReport.getHoursCalculation()).thenReturn(hoursCalculation);
        when(localIndividualReport.getTotalEarnings()).thenReturn(BigDecimal.valueOf(800));
        when(localIndividualReport.getTotalConsumptionAmount()).thenReturn(BigDecimal.valueOf(50));

        // Mock dependencies
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(employee)));
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId()))
                .thenReturn(localIndividualReport);

        // ✅ DEBUG: Verificar que el employee tiene paymentMethod
        System.out.println("🔍 [TEST DEBUG] Employee paymentMethod: " + employee.getPaymentMethod());
        System.out.println("🔍 [TEST DEBUG] Employee paymentMethod class: " +
                (employee.getPaymentMethod() != null ? employee.getPaymentMethod().getClass().getName() : "NULL"));

        EmployeeSummary employeeSummaryWithPayment = new EmployeeSummary(
                employee.getName(),
                localIndividualReport.getHoursCalculation().getTotalHours(),
                localIndividualReport.getTotalEarnings(),
                localIndividualReport.getTotalConsumptionAmount(),
                localIndividualReport.getTotalEarnings().subtract(localIndividualReport.getTotalConsumptionAmount()),
                achPaymentMethod
        );

        ManagerReport mockManagerReport = new ManagerReport(
                List.of(employeeSummaryWithPayment),
                new ReportTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
        );

        // Mock the behavior of managerReportCalculator.calculate
        when(managerReportCalculator.calculate(anyList(), anyList())).thenReturn(mockManagerReport);

        // When
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Then
        ArgumentCaptor<ManagerReport> managerReportCaptor = ArgumentCaptor.forClass(ManagerReport.class);
        verify(notificationPort).sendManagerReportByEmail(eq("manager@example.com"), managerReportCaptor.capture());

        ManagerReport capturedReport = managerReportCaptor.getValue();
        assertNotNull(capturedReport);
        assertFalse(capturedReport.getEmployeeSummaries().isEmpty());

        EmployeeSummary employeeSummary = capturedReport.getEmployeeSummaries().get(0);

        // ✅ DEBUG: Verificar qué hay en el paymentMethod
        System.out.println("🔍 [TEST DEBUG] Captured paymentMethod: " + employeeSummary.getPaymentMethod());
        System.out.println("🔍 [TEST DEBUG] Captured paymentMethod class: " +
                (employeeSummary.getPaymentMethod() != null ? employeeSummary.getPaymentMethod().getClass().getName() : "NULL"));

        assertNotNull(employeeSummary.getPaymentMethod(), "PaymentMethod should not be null");
        assertTrue(employeeSummary.getPaymentMethod() instanceof AchPaymentMethod,
                "PaymentMethod should be instance of AchPaymentMethod");

        AchPaymentMethod capturedAchPaymentMethod = (AchPaymentMethod) employeeSummary.getPaymentMethod();
        assertEquals("Bank of America", capturedAchPaymentMethod.getBankName());
        assertEquals("123456789", capturedAchPaymentMethod.getAccountNumber());
        assertEquals(BankAccount.SAVINGS, capturedAchPaymentMethod.getBankAccountType());
    }
}
