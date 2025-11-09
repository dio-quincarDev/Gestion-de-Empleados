package com.employed.bar.infrastructure.adapter.out.pdf;

import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.model.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.time.LocalDate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorAdapterTest {

    private PdfGeneratorAdapter pdfGeneratorAdapter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pdfGeneratorAdapter = new PdfGeneratorAdapter();
    }

    /**
     * README: Para que este test funcione, necesitas crear un archivo de plantilla de Word en:
     * 'src/test/resources/templates/reporte_semanal_template.docx'
     *
     * Este archivo debe contener los siguientes placeholders para que el test los reemplace:
     * - ${startDate}
     * - ${endDate}
     *
     * También debe incluir dos tablas:
     * 1. Una tabla para los totales que contenga el texto "Horas Regulares" en el encabezado.
     * 2. Una tabla para los empleados que contenga el texto "Nombre" en el encabezado y una fila de ejemplo (plantilla) debajo del encabezado.
     */
    @Test
    void shouldGeneratePdfFromManagerReport() throws IOException {
        // Arrange
        LocalDate testStartDate = LocalDate.of(2023, 1, 1);
        LocalDate testEndDate = LocalDate.of(2023, 1, 7);

        ReportTotals totals = new ReportTotals(
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(450)
        );
        EmployeeSummary summary = new EmployeeSummary(
                "John Doe",
                BigDecimal.valueOf(45),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(450),
                new com.employed.bar.domain.model.payment.YappyPaymentMethod("6666-7777")
        );
        ManagerReport report = new ManagerReport(Collections.singletonList(summary), totals);

        // Act & Assert
        try {
            byte[] pdfBytes = pdfGeneratorAdapter.generateManagerReportPdf(report, testStartDate, testEndDate);

            // Assert that the PDF is not null or empty
            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 0);

            // (Opcional) Guarda el PDF generado para inspección manual
            File outputFile = tempDir.resolve("test_report.pdf").toFile();
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(pdfBytes);
            }
            System.out.println("PDF de prueba guardado en: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            // If any exception occurs, fail the test and print the stack trace
            fail("PDF generation failed with an exception: " + e.getMessage(), e);
        }
    }
}
