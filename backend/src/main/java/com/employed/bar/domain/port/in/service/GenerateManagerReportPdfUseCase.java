package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.manager.ManagerReport;

import java.time.LocalDate;

public interface GenerateManagerReportPdfUseCase {
    byte[] generateManagerReportPdf(LocalDate startDate, LocalDate endDate);
}
