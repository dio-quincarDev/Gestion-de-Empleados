package com.employed.bar.infrastructure.adapter.out.pdf;

import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Fuentes mejoradas con tamaños optimizados
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static final Font HIGHLIGHT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLUE);

    // Colores para mejor apariencia
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(79, 129, 189); // Azul profesional
    private static final BaseColor LIGHT_BLUE = new BaseColor(220, 230, 241);
    private static final BaseColor LIGHT_GRAY = new BaseColor(242, 242, 242);

    @Override
    public byte[] generateManagerReportPdf(ManagerReport managerReport, LocalDate startDate, LocalDate endDate) {
        try {
            return createProfessionalPdf(managerReport, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private byte[] createProfessionalPdf(ManagerReport report, LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Horizontal para tablas anchas
            PdfWriter.getInstance(document, pdfOut);

            document.open();
            addProfessionalContent(document, report, startDate, endDate);
            document.close();

            return pdfOut.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF creation failed", e);
        }
    }

    private void addProfessionalContent(Document document, ManagerReport report, LocalDate startDate, LocalDate endDate) throws DocumentException {
        // Header con logo y título (simulado)
        addHeaderSection(document);

        // Información de fechas
        addDateSection(document, startDate, endDate);

        // Tabla de resumen ejecutivo
        addExecutiveSummary(document, report.getTotals());

        // Tabla detallada de empleados
        addDetailedEmployeesTable(document, report.getEmployeeSummaries());

        // Footer con información adicional
        addFooterSection(document);
    }

    private void addHeaderSection(Document document) throws DocumentException {
        try {
            // Add logo
            Image logo = Image.getInstance(getClass().getClassLoader().getResource("static/images/1800-logo.png"));
            logo.scaleToFit(100, 100); // Scale image to fit
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception e) {
            System.err.println("Error adding logo: " + e.getMessage());
            // Continue without logo if there's an error
        }

        // Título principal
        Paragraph title = new Paragraph("PLANILLA DE COLABORADORES", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5f);
        document.add(title);

        // Subtítulo
        Paragraph subtitle = new Paragraph("INFORME SEMANAL DE NÓMINA", SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(15f);
        document.add(subtitle);
    }

    private void addDateSection(Document document, LocalDate startDate, LocalDate endDate) throws DocumentException {
        String formattedStartDate = startDate.format(DATE_FORMATTER);
        String formattedEndDate = endDate.format(DATE_FORMATTER);

        Paragraph periodInfo = new Paragraph();
        periodInfo.add(new Chunk("Período del Reporte: ", BOLD_FONT));
        periodInfo.add(new Chunk(formattedStartDate + " al " + formattedEndDate, NORMAL_FONT));
        periodInfo.setSpacingAfter(10f);
        document.add(periodInfo);

        Paragraph generationDate = new Paragraph(
                "Generado el: " + java.time.LocalDate.now().format(DATE_FORMATTER),
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC)
        );
        generationDate.setSpacingAfter(15f);
        document.add(generationDate);
    }

    private void addExecutiveSummary(Document document, ReportTotals totals) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("RESUMEN EJECUTIVO", BOLD_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(5f);
        summaryTable.setSpacingAfter(15f);

        // Filas del resumen con formato mejorado
        addSummaryRow(summaryTable, "Horas Regulares Trabajadas", totals.getTotalRegularHoursWorked() + " hrs");
        addSummaryRow(summaryTable, "Horas Extras Trabajadas", totals.getTotalOvertimeHoursWorked() + " hrs");
        addSummaryRow(summaryTable, "Total Horas",
                totals.getTotalRegularHoursWorked().add(totals.getTotalOvertimeHoursWorked()) + " hrs");
        addSummaryRow(summaryTable, "Obligación Salarial", "$" + totals.getTotalEarnings());
        addSummaryRow(summaryTable, "Consumos Totales", "$" + totals.getTotalConsumptions());
        addSummaryRow(summaryTable, "Pago Neto Total", "$" + totals.getTotalNetPay(), true);

        document.add(summaryTable);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        addSummaryRow(table, label, value, false);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, boolean highlight) {
        Font valueFont = highlight ? HIGHLIGHT_FONT : BOLD_FONT;
        BaseColor bgColor = highlight ? LIGHT_BLUE : BaseColor.WHITE;

        PdfPCell labelCell = createStyledCell(label, BOLD_FONT, bgColor, Element.ALIGN_LEFT);
        PdfPCell valueCell = createStyledCell(value, valueFont, bgColor, Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addDetailedEmployeesTable(Document document, List<EmployeeSummary> employees) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("DETALLE POR COLABORADOR", BOLD_FONT);
        sectionTitle.setSpacingAfter(10f);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(20f);

        // Configurar anchos de columnas optimizados
        float[] columnWidths = {25f, 12f, 15f, 15f, 15f, 18f};
        table.setWidths(columnWidths);

        // Encabezados con estilo profesional
        addProfessionalHeader(table, "NOMBRE");
        addProfessionalHeader(table, "HORAS");
        addProfessionalHeader(table, "SUELDO");
        addProfessionalHeader(table, "CONSUMOS");
        addProfessionalHeader(table, "PAGO NETO");
        addProfessionalHeader(table, "MÉTODO DE PAGO");

        // Datos de empleados con estilo zebra
        boolean alternate = false;
        for (EmployeeSummary employee : employees) {
            BaseColor rowColor = alternate ? LIGHT_GRAY : BaseColor.WHITE;
            alternate = !alternate;

            addEmployeeDataRow(table, employee, rowColor);
        }

        document.add(table);
    }

    private void addProfessionalHeader(PdfPTable table, String header) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, HEADER_FONT));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setBackgroundColor(HEADER_BG_COLOR);
        headerCell.setBorderWidth(1f);
        headerCell.setPadding(8f);
        headerCell.setMinimumHeight(25f);
        table.addCell(headerCell);
    }

    private void addEmployeeDataRow(PdfPTable table, EmployeeSummary employee, BaseColor bgColor) {
        table.addCell(createStyledCell(employee.getEmployeeName(), NORMAL_FONT, bgColor, Element.ALIGN_LEFT));
        table.addCell(createStyledCell(employee.getTotalHoursWorked() + " hrs", NORMAL_FONT, bgColor, Element.ALIGN_CENTER));
        table.addCell(createStyledCell("$" + employee.getTotalEarnings(), NORMAL_FONT, bgColor, Element.ALIGN_RIGHT));
        table.addCell(createStyledCell("$" + employee.getTotalConsumptions(), NORMAL_FONT, bgColor, Element.ALIGN_RIGHT));
        table.addCell(createStyledCell("$" + employee.getNetPay(), BOLD_FONT, bgColor, Element.ALIGN_RIGHT));
        table.addCell(createStyledCell(employee.getPaymentMethod().getType().toString(), NORMAL_FONT, bgColor, Element.ALIGN_CENTER));
    }

    private PdfPCell createStyledCell(String content, Font font, BaseColor bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setBorderWidth(0.5f);
        cell.setPadding(6f);
        cell.setBorderColor(BaseColor.GRAY);
        return cell;
    }

    private void addFooterSection(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
                "Este reporte fue generado automáticamente por el Sistema de Gestión de Nómina - " +
                        java.time.Year.now().getValue(),
                new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);
    }
}