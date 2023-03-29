package com.example.demo.services.impl;

import com.example.demo.entities.Contact;
import com.example.demo.helper.ExcelHelper;
import com.example.demo.repositories.ContactRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.ExcelService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private UserService userService;

    @Override
    public ByteArrayInputStream getData(int userId) {
        List<Contact> contacts = userService.getSingleUserById(userId).getContacts();
        ByteArrayInputStream byteArrayInputStream= null;
        try {
            byteArrayInputStream = ExcelHelper.dataToExcel(contacts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayInputStream;
    }
}
