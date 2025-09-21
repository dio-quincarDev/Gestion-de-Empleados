package com.employed.bar.service;

import com.employed.bar.application.service.ManagerReportApplicationService;
import com.employed.bar.domain.model.manager.ManagerReport;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(reportingUseCase.generateCompleteReport(startDate, endDate, employee)).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);

        // Act
        managerReportApplicationService.generateAndSendManagerReport(startDate, endDate);

        // Assert
        verify(employeeRepository, times(1)).findAll();
        verify(reportingUseCase, times(1)).generateCompleteReport(startDate, endDate, employee);
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
        when(reportingUseCase.generateCompleteReport(startDate, endDate, employee)).thenReturn(individualReport);
        when(managerReportCalculator.calculate(employees, individualReports)).thenReturn(managerReport);
        when(pdfGeneratorPort.generateManagerReportPdf(managerReport)).thenReturn(expectedPdf);

        // Act
        byte[] actualPdf = managerReportApplicationService.generateManagerReportPdf(startDate, endDate);

        // Assert
        assertNotNull(actualPdf);
        assertArrayEquals(expectedPdf, actualPdf);
        verify(employeeRepository, times(1)).findAll();
        verify(reportingUseCase, times(1)).generateCompleteReport(startDate, endDate, employee);
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
        assertThrows(NullPointerException.class, () -> {
            managerReportApplicationService.generateAndSendManagerReport(null, endDate);
        });

        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, notificationPort);
    }

    @Test
    void testGenerateAndSendManagerReport_NullEndDate() {
        assertThrows(NullPointerException.class, () -> {
            managerReportApplicationService.generateAndSendManagerReport(startDate, null);
        });

        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, notificationPort);
    }

    @Test
    void testGenerateManagerReportPdf_NullStartDate() {
        assertThrows(NullPointerException.class, () -> {
            managerReportApplicationService.generateManagerReportPdf(null, endDate);
        });

        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, pdfGeneratorPort);
    }

    @Test
    void testGenerateManagerReportPdf_NullEndDate() {
        assertThrows(NullPointerException.class, () -> {
            managerReportApplicationService.generateManagerReportPdf(startDate, null);
        });

        verifyNoInteractions(employeeRepository, reportingUseCase, managerReportCalculator, pdfGeneratorPort);
    }
}