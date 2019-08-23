package com.example.testdemo.utils;

import com.example.testdemo.entity.BillEntity;

import java.math.BigDecimal;

public abstract class AppUtils {

    public AppUtils() {
    }

    public static BillEntity getBill(String id) {
        return BillEntity.builder().id("AppUtils:Static:" + id).cost(BigDecimal.TEN).build();
    }
}
