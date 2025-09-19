package com.employed.bar.domain.port.in.service;

import java.time.LocalDate;

public interface ManagerReportServicePort {
    void generateAndSendManagerReport(LocalDate startDate, LocalDate endDate);
    byte[] generateManagerReportPdf(LocalDate startDate, LocalDate endDate);
}
