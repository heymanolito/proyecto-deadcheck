package com.example.application.backend.data.util;

import java.util.Locale;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import static com.itextpdf.text.FontFactory.HELVETICA;
import static com.itextpdf.text.FontFactory.HELVETICA_BOLD;
public class EmployeeControlReport extends AbstractReport {

    protected EmployeeControlReport(Locale locale) {
        super(locale);
    }

    public void addCategoryTitleCellWithColspan(PdfPTable table, String text, int colspan) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA_BOLD, 12f)));
        cell.setBorder(0);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    public void addResultsCell(PdfPTable table, String text) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, 7f)));
        cell.setColspan(5);
        cell.setBorder(0);
        cell.setPaddingBottom(8f);
        table.addCell(cell);
    }

}
