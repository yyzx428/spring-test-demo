package com.example.testdemo.services;

import com.example.testdemo.entity.BillEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillService {

    public BillEntity getBill(String id) {
        return BillEntity.builder().id("BillService:" + id).cost(BigDecimal.TEN).build();
    }
}
