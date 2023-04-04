package com.example.demo.helper;

import com.example.demo.entities.Contact;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {

    public static String[] HEADERS={"CID","NAME","SECOND NAME","WORK","EMAIL","PHONE","IMAGE","DESCRIPTION"};

    public static String SHEET_NAME="contacts_data";

    public static ByteArrayInputStream dataToExcel(List<Contact> list) throws IOException {

        //create work book
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try{

            //create sheet
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            //create row:header row
            Row row = sheet.createRow(0);
            for(int i=0;i<HEADERS.length;i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            //create row:value row
            int rowIndex=1;
            for(Contact c:list){
                Row dataRow = sheet.createRow(rowIndex);
                rowIndex++;
                dataRow.createCell(0).setCellValue(c.getCId());
                dataRow.createCell(1).setCellValue(c.getName());
                dataRow.createCell(2).setCellValue(c.getSecondName());
                dataRow.createCell(3).setCellValue(c.getWork());
                dataRow.createCell(4).setCellValue(c.getEmail());
                dataRow.createCell(5).setCellValue(c.getPhone());
                dataRow.createCell(6).setCellValue(c.getImage());
                dataRow.createCell(7).setCellValue(c.getDescription());
            }

            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            workbook.close();
            outputStream.close();
        }
    }

}
