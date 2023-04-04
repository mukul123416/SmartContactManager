package com.example.demo.helper;

import com.example.demo.entities.User;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AdminPdfHelper {

    private List<User> users;

    private User user;

    public AdminPdfHelper(List<User> users, User user) {
        this.users = users;
        this.user = user;
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLACK);
        cell.setPadding(15);
        cell.setPaddingLeft(10);

        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("UID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("NAME", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("ROLE", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("STATUS", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("EMAIL", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("IMAGE", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("ABOUT", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) throws IOException {
        for (User user1 : users) {
            if(user1.getRole().equals("ROLE_USER")){
                com.lowagie.text.Image img = com.lowagie.text.Image.getInstance("target/classes/static/img/"+user1.getImageUrl());
                table.addCell(String.valueOf(user1.getId()));
                table.addCell(user1.getName());
                table.addCell(user1.getRole());
                if(user1.isEnabled()){
                    table.addCell("Active");
                }else{
                    table.addCell("NonActive");
                }
                table.addCell(user1.getEmail());
                table.addCell(img);
                table.addCell(user1.getAbout());
            }
        }
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {

        Document document = null;

        document = new Document(PageSize.B0);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.setMargins(-2000, -2000, 100, 0);

        document.open();

        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(40);
        font.setColor(Color.black);

        com.lowagie.text.Font courier = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 15, com.lowagie.text.Font.NORMAL);
        com.lowagie.text.Font bold = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 15, com.lowagie.text.Font.BOLD);
        com.lowagie.text.Font georgia = new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 16, Font.BOLD);

        Paragraph p = new Paragraph("List of Users", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        com.lowagie.text.Image img = Image.getInstance("target/classes/static/img/"+user.getImageUrl());
        img.setAlignment(Element.ALIGN_RIGHT);
        img.scaleAbsolute(100, 100);
        Chunk c10 = new Chunk("Admin: ", bold);
        Chunk c11 = new Chunk(""+user.getName()+"\n\n", courier);
        Chunk c12 = new Chunk("Admin Id: ", bold);
        Chunk c13 = new Chunk(""+user.getId()+"\n\n", courier);
        Chunk c14 = new Chunk("Email: ", bold);
        Chunk c15 = new Chunk(user.getEmail(), courier);

        Paragraph p2 = new Paragraph();

        p2.add(c10);
        p2.add(c11);
        p2.add(c12);
        p2.add(c13);
        p2.add(c14);
        p2.add(c15);

        PdfPTable table = new PdfPTable(2);
        PdfPTable imagetable = new PdfPTable(1);
        PdfPTable b = new PdfPTable(1);
        PdfPCell cell = new PdfPCell();
        cell.setBorder( com.lowagie.text.Rectangle.NO_BORDER );
        cell.setPaddingLeft(2220f);
        imagetable.addCell(cell);
        document.add(imagetable);

        Paragraph br=new Paragraph("\n\n");
        PdfPCell brcell = new PdfPCell();
        brcell.addElement(br);
        brcell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        b.addCell(brcell);
        b.setWidthPercentage(15f);
        document.add(b);

        PdfPCell cell1 = new PdfPCell();
        cell1.addElement(p2);
        cell1.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell();
        cell2.addElement(img);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.setWidthPercentage(15f);
        document.add(table);

        PdfPTable Tabledata = new PdfPTable(7);
        Tabledata.setWidthPercentage(15f);
        Tabledata.setSpacingBefore(50);

        Tabledata.getDefaultCell().setPadding(10);

        writeTableHeader(Tabledata);
        writeTableData(Tabledata);

        document.add(Tabledata);
        document.close();

    }
}

