package com.example.demo.controllers;

import com.example.demo.entities.Contact;
import com.example.demo.entities.MyOrder;
import com.example.demo.entities.User;
import com.example.demo.helper.AdminPdfHelper;
import com.example.demo.helper.AllTransactionPdfHelper;
import com.example.demo.helper.PdfHelper;
import com.example.demo.helper.SingleTransactionPdfHelper;
import com.example.demo.payloads.ErrorResponse;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

@RestController
@CrossOrigin
public class PdfController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MyOrderRepository myOrderRepository;

    Random random = new Random();

    @GetMapping("contacts/export/pdf")
    public ResponseEntity<?> exportToPDF(HttpServletResponse response, Principal principal) throws IOException {
        try{
            String userName=principal.getName();
            User user = userRepository.getUserByUserName(userName);
            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=contacts_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);

            List<Contact> contacts = userService.getSingleUserById(user.getId()).getContacts();
            PdfHelper pdfHelper = new PdfHelper(contacts,user);
            pdfHelper.export(response);
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ErrorResponse.ResponseHandler(e.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("users/export/pdf")
    public ResponseEntity<?> userExportToPDF(HttpServletResponse response, Principal principal) throws IOException {
        try{
            String userName=principal.getName();
            User user = userRepository.getUserByUserName(userName);
            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);

            List<User> users = userService.getAllUser();
            AdminPdfHelper adminPdfHelper = new AdminPdfHelper(users,user);
            adminPdfHelper.export(response);
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ErrorResponse.ResponseHandler(e.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("singleTransaction/export/pdf/{tId}")
    public ResponseEntity<?> singleTransactionExportToPDF(@PathVariable("tId") Long tId,HttpServletResponse response) throws IOException {
        try{

            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=singleTransaction_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);

            MyOrder myOrder = myOrderRepository.getById(tId);

            SingleTransactionPdfHelper singleTransactionPdfHelper = new SingleTransactionPdfHelper(myOrder);
            singleTransactionPdfHelper.export(response);
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ErrorResponse.ResponseHandler(e.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("AllTransaction/export/pdf")
    public ResponseEntity<?> AllTransactionExportToPDF(HttpServletResponse response,Principal principal) throws IOException {
        try{

            String userName=principal.getName();
            User user = userRepository.getUserByUserName(userName);
            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=allTransaction_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);

            List<MyOrder> myOrders = this.myOrderRepository.findAll();

            AllTransactionPdfHelper allTransactionPdfHelper = new AllTransactionPdfHelper(myOrders,user);
            allTransactionPdfHelper.export(response);
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ErrorResponse.ResponseHandler(e.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }
}
