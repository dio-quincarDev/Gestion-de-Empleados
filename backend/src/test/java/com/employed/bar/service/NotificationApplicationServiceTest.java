package com.employed.bar.service;

import com.employed.bar.application.notification.NotificationApplicationService;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

class NotificationApplicationServiceTest {

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private NotificationApplicationService notificationApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendReport() {
        // Given
        EmployeeClass employee = new EmployeeClass(); // Customize as needed
        Report report = new Report(); // Customize as needed
        List<EmployeeClass> employees = Collections.singletonList(employee);
        List<Report> reports = Collections.singletonList(report);

        // When
        notificationApplicationService.sendReport(employees, reports);

        // Then
        verify(notificationPort).sendReportByEmail(employee, report);
    }
}
