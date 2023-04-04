package com.example.demo.services.impl;

import com.example.demo.entities.MyOrder;
import com.example.demo.helper.AllTransactionsExcelHelper;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.services.AllTransactionsExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class AllTransactionsExcelServiceImpl implements AllTransactionsExcelService {

    @Autowired
    private MyOrderRepository myOrderRepository;

    @Override
    public ByteArrayInputStream getData() {
        List<MyOrder> all = this.myOrderRepository.findAll();
        ByteArrayInputStream byteArrayInputStream= null;
        try {
            byteArrayInputStream = AllTransactionsExcelHelper.dataToExcel(all);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayInputStream;
    }
}
