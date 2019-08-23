package com.example.testdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillEntity {

    /**
     * 订单Id
     * */
    private String id;

    /**
     * 消耗金额
     * */
    private BigDecimal cost;

}
