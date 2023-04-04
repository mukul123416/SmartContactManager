package com.example.demo.helper;
import com.example.demo.entities.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AdminExcelHelper {

    public static String[] HEADERS={"UID","NAME","ROLE","STATUS","EMAIL","ABOUT"};

    public static String SHEET_NAME="users_data";

    public static ByteArrayInputStream dataToExcel(List<User> users) throws IOException {

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
            for(User u:users){
                if(u.getRole().equals("ROLE_USER")){
                    Row dataRow = sheet.createRow(rowIndex);
                    rowIndex++;
                    dataRow.createCell(0).setCellValue(u.getId());
                    dataRow.createCell(1).setCellValue(u.getName());
                    dataRow.createCell(2).setCellValue(u.getRole());
                    if(u.isEnabled()){
                        dataRow.createCell(3).setCellValue("Active");
                    }else {
                        dataRow.createCell(3).setCellValue("NonActive");
                    }
                    dataRow.createCell(4).setCellValue(u.getEmail());
                    dataRow.createCell(5).setCellValue(u.getAbout());
                }
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
