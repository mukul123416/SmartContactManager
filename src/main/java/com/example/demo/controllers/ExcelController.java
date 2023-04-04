package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AdminExcelService;
import com.example.demo.services.AllTransactionsExcelService;
import com.example.demo.services.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
@CrossOrigin
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private AdminExcelService adminExcelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllTransactionsExcelService allTransactionsExcelService;


    @GetMapping("contacts/export/excel")
    public ResponseEntity<Resource> ExcelDownload(Principal principal) throws IOException {
        String userName=principal.getName();
        User user = userRepository.getUserByUserName(userName);
        String filename="contacts.xlsx";
        ByteArrayInputStream byteArrayInputStream = excelService.getData(user.getId());
        InputStreamResource file = new InputStreamResource(byteArrayInputStream);
        ResponseEntity<Resource> body = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        return body;
    }

    @GetMapping("users/export/excel")
    public ResponseEntity<Resource> usersExcelDownload(Principal principal) throws IOException {
        String userName=principal.getName();
        User user = userRepository.getUserByUserName(userName);
        String filename="users.xlsx";
        ByteArrayInputStream byteArrayInputStream = adminExcelService.getData();
        InputStreamResource file = new InputStreamResource(byteArrayInputStream);
        ResponseEntity<Resource> body = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        return body;
    }

    @GetMapping("allTransactions/export/excel")
    public ResponseEntity<Resource> allTransactionExcelDownload(Principal principal) throws IOException {
        String userName=principal.getName();
        User user = userRepository.getUserByUserName(userName);
        String filename="allTransactions.xlsx";
        ByteArrayInputStream byteArrayInputStream = allTransactionsExcelService.getData();
        InputStreamResource file = new InputStreamResource(byteArrayInputStream);
        ResponseEntity<Resource> body = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        return body;
    }
}
