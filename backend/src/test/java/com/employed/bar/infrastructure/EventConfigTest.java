package com.employed.bar.infrastructure;

import com.employed.bar.domain.event.TestEmailRequestedEvent;
import com.employed.bar.domain.event.WeeklyReportRequestedEvent;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.infrastructure.config.EventConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventConfigTest {

    @Mock
    private ReportingUseCase reportingUseCase;

    @InjectMocks
    private EventConfig eventConfig;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);
    }

    @Test
    void testHandleWeeklyReportRequest() {
        WeeklyReportRequestedEvent event = new WeeklyReportRequestedEvent(this, startDate, endDate);

        eventConfig.handleWeeklyReportRequest(event);

        verify(reportingUseCase, times(1)).generateAndSendWeeklyReport(startDate, endDate);
    }

    @Test
    void testHandleTestEmailRequest() {
        Long employeeId = 1L;
        TestEmailRequestedEvent event = new TestEmailRequestedEvent(this, employeeId);

        eventConfig.handleTestEmailRequest(event);

        verify(reportingUseCase, times(1)).sendTestEmailToEmployee(employeeId);
    }
}
