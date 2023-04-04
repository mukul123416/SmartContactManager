package com.example.demo.services.impl;

import com.example.demo.entities.User;
import com.example.demo.helper.AdminExcelHelper;
import com.example.demo.services.AdminExcelService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class AdminExcelServiceImpl implements AdminExcelService {
    @Autowired
    private UserService userService;

    @Override
    public ByteArrayInputStream getData() {
        List<User> allUser = this.userService.getAllUser();
        ByteArrayInputStream byteArrayInputStream= null;
        try {
            byteArrayInputStream = AdminExcelHelper.dataToExcel(allUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayInputStream;
    }
}
