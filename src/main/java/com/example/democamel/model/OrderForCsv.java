package com.example.democamel.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderForCsv {

    private String country;
    private BigDecimal orderCount;
    private BigDecimal averageUnitsSold;
    private BigDecimal averageUnitPrice;
    private BigDecimal averageUnitCost;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;

}
