package com.example.demo.helper;

import com.example.demo.entities.MyOrder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AllTransactionsExcelHelper {

    static NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static String[] HEADERS={"TID","ORDER ID","PAYMENT ID","AMOUNT","RECEIPT","STATUS","CREATED AT"};

    public static String SHEET_NAME="allTransactions_data";

    public static ByteArrayInputStream dataToExcel(List<MyOrder> myOrders) throws IOException {

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
            for(MyOrder u:myOrders){
                    Row dataRow = sheet.createRow(rowIndex);
                    rowIndex++;
                    dataRow.createCell(0).setCellValue(u.getMyOrderId());
                    dataRow.createCell(1).setCellValue(u.getOrderId());
                    if(u.getPaymentId()==null){
                        dataRow.createCell(2).setCellValue("null");
                    }else{
                        dataRow.createCell(2).setCellValue(u.getPaymentId());
                    }
                    dataRow.createCell(3).setCellValue("Rs."+formatter.format(u.getAmount()/100));
                    dataRow.createCell(4).setCellValue(u.getReceipt());
                    dataRow.createCell(5).setCellValue(u.getStatus());
                    dataRow.createCell(6).setCellValue(u.getCreatedAt().toString());
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
