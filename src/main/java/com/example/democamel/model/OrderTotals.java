package com.example.democamel.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderTotals {

    private String region;
    private String country;
    private BigDecimal unitsSoldTotal;
    private BigDecimal unitsPriceTotal;
    private BigDecimal unitCostTotal;
    private BigDecimal totalOrders;

}
