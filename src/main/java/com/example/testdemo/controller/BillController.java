package com.example.testdemo.controller;

import com.example.testdemo.entity.BillEntity;
import com.example.testdemo.services.BillService;
import com.example.testdemo.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("getBill")
    public BillEntity getBill(@RequestParam("id") String id) {
        return billService.getBill(id);
    }

    @GetMapping("getBillStatic")
    public BillEntity getBillStatic(@RequestParam("id") String id) {
        return AppUtils.getBill(id);
    }

    @GetMapping("getBillSystem")
    public BillEntity getBillSystem(@RequestParam("id") String id) {
        return BillEntity.builder()
                .id("System:" + System.currentTimeMillis() + ":" + id)
                .cost(BigDecimal.TEN).build();
    }
}
