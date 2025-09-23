package com.employed.bar.infrastructure.adapter.out.pdf;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generateManagerReportPdf(ManagerReport managerReport) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Manager Weekly Report"));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Employee Summaries:"));
            for (EmployeeSummary summary : managerReport.getEmployeeSummaries()) {
                document.add(new Paragraph("  Employee Name: " + summary.getEmployeeName()));
                document.add(new Paragraph("  Total Hours Worked: " + summary.getTotalHoursWorked()));
                document.add(new Paragraph("  Total Earnings: " + summary.getTotalEarnings()));
                document.add(new Paragraph("  Total Consumptions: " + summary.getTotalConsumptions()));
                document.add(new Paragraph("  Net Pay: " + summary.getNetPay()));
                document.add(new Paragraph("\n"));
            }

            document.add(new Paragraph("Totals:"));
            ReportTotals totals = managerReport.getTotals();
            document.add(new Paragraph("  Total Regular Hours Worked: " + totals.getTotalRegularHoursWorked()));
            document.add(new Paragraph("  Total Overtime Hours Worked: " + totals.getTotalOvertimeHoursWorked()));
            document.add(new Paragraph("  Total Earnings: " + totals.getTotalEarnings()));
            document.add(new Paragraph("  Total Consumptions: " + totals.getTotalConsumptions()));
            document.add(new Paragraph("  Total Net Pay: " + totals.getTotalNetPay()));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return out.toByteArray();
    }
}
