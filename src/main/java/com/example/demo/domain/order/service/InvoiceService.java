package com.example.demo.domain.order.service;

import com.example.demo.domain.order.entity.Order;
import com.example.demo.domain.order.entity.OrderItem;
import com.example.demo.domain.order.repository.OrderRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final OrderRepository orderRepository;
    private static final String TEMPLATE_PATH = "src/main/resources/templates/invoice_template.xlsx";

    @PostConstruct
    public void init() {
        // Create a dummy template if it doesn't exist
        File file = new File(TEMPLATE_PATH);
        if (!file.exists()) {
            try {
                // Ensure directory exists
                file.getParentFile().mkdirs();
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Invoice");
                    
                    // Create some basic structure
                    Row row0 = sheet.createRow(0);
                    row0.createCell(0).setCellValue("INVOICE");
                    
                    Row row1 = sheet.createRow(1);
                    row1.createCell(0).setCellValue("Order ID:");
                    // Cell 1 will be filled with Order ID
                    
                    Row row2 = sheet.createRow(2);
                    row2.createCell(0).setCellValue("Date:");
                    // Cell 1 will be filled with Date
                    
                    Row row3 = sheet.createRow(3); // Empty row
                    
                    Row row4 = sheet.createRow(4);
                    row4.createCell(0).setCellValue("Item");
                    row4.createCell(1).setCellValue("Quantity");
                    row4.createCell(2).setCellValue("Price");
                    row4.createCell(3).setCellValue("Total");
                    
                    // Order items will start from row 5
                    
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] generateInvoicePdf(Long orderId) throws IOException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 1. Read Excel Template and Populate Data
        try (FileInputStream fis = new FileInputStream(TEMPLATE_PATH);
             Workbook workbook = WorkbookFactory.create(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Fill Header Info
            Row rowId = sheet.getRow(1);
            if (rowId == null) rowId = sheet.createRow(1);
            rowId.createCell(1).setCellValue(order.getId());
            
            Row rowDate = sheet.getRow(2);
            if (rowDate == null) rowDate = sheet.createRow(2);
            rowDate.createCell(1).setCellValue(
                order.getOrderDate() != null ? 
                order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""
            );

            // Fill Items (This is a simplified example where we just overwrite rows)
            int rowNum = 5;
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) row = sheet.createRow(rowNum);
                    
                    row.createCell(0).setCellValue("Book ID " + item.getBookId());
                    row.createCell(1).setCellValue(item.getQuantity());
                    row.createCell(2).setCellValue(item.getUnitPrice().doubleValue());
                    row.createCell(3).setCellValue(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())).doubleValue());
                    
                    rowNum++;
                }
            }
            
            // Add Total
            Row totalRow = sheet.createRow(rowNum + 1);
            totalRow.createCell(2).setCellValue("Grand Total:");
            totalRow.createCell(3).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0);

            // 2. Convert Excel Data to PDF using OpenPDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            document.add(new Paragraph("Invoice Generated from Excel", new Font(Font.HELVETICA, 18, Font.BOLD)));
            document.add(new Paragraph(" ")); // Spacer

            // Create PDF Table based on Excel content logic
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Iterate over the rows we care about (HEADER)
            addPdfCell(table, "Order ID: " + order.getId(), 4);
            addPdfCell(table, "Date: " + (order.getOrderDate() != null ? order.getOrderDate().toLocalDate() : ""), 4);
            addPdfCell(table, " ", 4);
            
            // Headers
            addPdfCell(table, "Item", 1);
            addPdfCell(table, "Quantity", 1);
            addPdfCell(table, "Price", 1);
            addPdfCell(table, "Total", 1);

            // Items
             if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    addPdfCell(table, "Book ID " + item.getBookId(), 1);
                    addPdfCell(table, String.valueOf(item.getQuantity()), 1);
                    addPdfCell(table, String.valueOf(item.getUnitPrice()), 1);
                    addPdfCell(table, String.valueOf(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))), 1);
                }
            }
             
            // Total
            addPdfCell(table, "", 2);
            addPdfCell(table, "Grand Total:", 1);
            addPdfCell(table, String.valueOf(order.getTotalAmount()), 1);

            document.add(table);
            document.close();
            
            return out.toByteArray();
        }
    }
    
    private void addPdfCell(PdfPTable table, String text, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setColspan(colspan);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
