package com.employed.bar.service;

import com.employed.bar.application.service.ManagerReportApplicationService;
import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
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

import java.math.BigDecimal;
import java.time.LocalDate;
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

        when(employeeRepository.findAll()).thenReturn(employees);
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId())).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert
        verify(employeeRepository, times(1)).findAll();
        verify(reportingUseCase, times(1)).generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());
        verify(managerReportCalculator, times(1)).calculate(employees, individualReports);
        verify(notificationPort, times(1)).sendManagerReportByEmail(managerEmail, managerReport);
    }

    @Test
    void testGenerateAndSendManagerReport_NoEmployees() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        when(managerReportCalculator.calculate(any(), any())).thenReturn(managerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert
        verify(employeeRepository, times(1)).findAll();
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

        when(employeeRepository.findAll()).thenReturn(employees);
        when(reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId())).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);
        when(pdfGeneratorPort.generateManagerReportPdf(managerReport)).thenReturn(expectedPdf);

        // Act
        byte[] actualPdf = managerReportApplicationService.generateManagerReportPdf(startDate, endDate);

        // Assert
        assertNotNull(actualPdf);
        assertArrayEquals(expectedPdf, actualPdf);
        verify(employeeRepository, times(1)).findAll();
        verify(reportingUseCase, times(1)).generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());
        verify(managerReportCalculator, times(1)).calculate(employees, individualReports);
        verify(pdfGeneratorPort, times(1)).generateManagerReportPdf(managerReport);
    }

    @Test
    void testGenerateManagerReportPdf_NoEmployees() {
        // Arrange
        byte[] expectedPdf = {1, 2, 3, 4};
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        when(managerReportCalculator.calculate(any(), any())).thenReturn(managerReport);
        when(pdfGeneratorPort.generateManagerReportPdf(managerReport)).thenReturn(expectedPdf);

        // Act
        byte[] actualPdf = managerReportApplicationService.generateManagerReportPdf(startDate, endDate);

        // Assert
        assertNotNull(actualPdf);
        assertArrayEquals(expectedPdf, actualPdf);
        verify(employeeRepository, times(1)).findAll();
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
    void testGenerateAndSendManagerReport_VerifiesManagerReportContent() {
        // Arrange
        EmployeeClass emp1 = new EmployeeClass();
        emp1.setId(1L);
        emp1.setName("Employee One");

        Report rep1 = mock(Report.class);


        List<EmployeeClass> employees = Collections.singletonList(emp1);
        List<Report> individualReports = Collections.singletonList(rep1);

        // Create a concrete ManagerReport to be returned by the mock calculator
        ManagerReport expectedManagerReport = new ManagerReport(
                Collections.singletonList(new com.employed.bar.domain.model.manager.EmployeeSummary(
                        "Employee One",
                        new BigDecimal("40.00"),
                        new BigDecimal("400.00"),
                        new BigDecimal("50.00"),
                        new BigDecimal("350.00")
                )),
                new com.employed.bar.domain.model.manager.ReportTotals(
                        new BigDecimal("40.00"),
                        BigDecimal.ZERO,
                        new BigDecimal("400.00"),
                        new BigDecimal("50.00"),
                        new BigDecimal("350.00")
                )
        );

        when(employeeRepository.findAll()).thenReturn(employees);
        when(reportingUseCase.generateCompleteReportForEmployeeById(any(LocalDate.class), any(LocalDate.class), anyLong())).thenReturn(rep1);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(expectedManagerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert - Usar ArgumentCaptor para verificar el contenido
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ManagerReport> reportCaptor = ArgumentCaptor.forClass(ManagerReport.class);

        verify(notificationPort, times(1)).sendManagerReportByEmail(emailCaptor.capture(), reportCaptor.capture());

        // Verificar el email
        assertEquals("manager@example.com", emailCaptor.getValue());

        // Verificar el contenido del reporte
        ManagerReport actualReport = reportCaptor.getValue();
        assertNotNull(actualReport);
        assertEquals(expectedManagerReport.getEmployeeSummaries().size(), actualReport.getEmployeeSummaries().size());

        // Verificar detalles del EmployeeSummary
        EmployeeSummary expectedSummary = expectedManagerReport.getEmployeeSummaries().get(0);
        EmployeeSummary actualSummary = actualReport.getEmployeeSummaries().get(0);

        assertEquals(expectedSummary.getEmployeeName(), actualSummary.getEmployeeName());
        assertEquals(0, expectedSummary.getTotalHoursWorked().compareTo(actualSummary.getTotalHoursWorked()));
        assertEquals(0, expectedSummary.getTotalEarnings().compareTo(actualSummary.getTotalEarnings()));
        assertEquals(0, expectedSummary.getTotalConsumptions().compareTo(actualSummary.getTotalConsumptions()));
        assertEquals(0, expectedSummary.getNetPay().compareTo(actualSummary.getNetPay()));

        // Verificar los totals
        ReportTotals expectedTotals = expectedManagerReport.getTotals();
        ReportTotals actualTotals = actualReport.getTotals();

        assertEquals(0, expectedTotals.getTotalRegularHoursWorked().compareTo(actualTotals.getTotalRegularHoursWorked()));
        assertEquals(0, expectedTotals.getTotalOvertimeHoursWorked().compareTo(actualTotals.getTotalOvertimeHoursWorked()));
        assertEquals(0, expectedTotals.getTotalEarnings().compareTo(actualTotals.getTotalEarnings()));
        assertEquals(0, expectedTotals.getTotalConsumptions().compareTo(actualTotals.getTotalConsumptions()));
        assertEquals(0, expectedTotals.getTotalNetPay().compareTo(actualTotals.getTotalNetPay()));
    }

    @Test
    void testGenerateManagerReportPdf_NullStartDate() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            managerReportApplicationService.generateManagerReportPdf(null, endDate);
        });
        assertEquals("Start date and end date must not be null", thrown.getMessage());
        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, pdfGeneratorPort);
    }

    @Test
    void testGenerateManagerReportPdf_NullEndDate() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            managerReportApplicationService.generateManagerReportPdf(startDate, null);
        });
        assertEquals("Start date and end date must not be null", thrown.getMessage());
        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, pdfGeneratorPort);
    }
}