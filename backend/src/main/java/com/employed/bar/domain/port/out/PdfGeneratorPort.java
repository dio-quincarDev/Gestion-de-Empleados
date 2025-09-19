package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.manager.ManagerReport;

public interface PdfGeneratorPort {
    byte[] generateManagerReportPdf(ManagerReport managerReport);
}
