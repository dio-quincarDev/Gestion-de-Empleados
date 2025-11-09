package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.manager.ManagerReport;

import java.time.LocalDate;

public interface PdfGeneratorPort {
    byte[] generateManagerReportPdf(ManagerReport managerReport, LocalDate startDate, LocalDate endDate);
}
