package com.example.demo.service;

import com.example.demo.entity.User;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayInputStream userReport(List<User> users) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 14, java.awt.Color.BLACK);
            Paragraph para = new Paragraph("User List Report", font);
            para.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 3, 3});

            addTableHeader(table);
            addRows(table, users);

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table) {
        table.addCell(createCell("ID"));
        table.addCell(createCell("Name"));
        table.addCell(createCell("Email"));
        table.addCell(createCell("Role"));
    }

    private PdfPCell createCell(String title) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        header.setPadding(5);
        header.setPhrase(new Phrase(title));
        return header;
    }

    private void addRows(PdfPTable table, List<User> users) {
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getName());
            table.addCell(user.getEmail());
            table.addCell(user.getRole());
        }
    }
}
