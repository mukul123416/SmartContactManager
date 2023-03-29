package com.example.demo.controllers;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.helper.PdfHelper;
import com.example.demo.payloads.ErrorResponse;
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
            return ErrorResponse.ResponseHandler(e.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }
}
