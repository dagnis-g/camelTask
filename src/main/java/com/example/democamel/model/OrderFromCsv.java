package com.example.democamel.model;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@CsvRecord(separator = ",", skipFirstLine = true)
public class OrderFromCsv implements Serializable {

    @DataField(pos = 1)
    private String region;
    @DataField(pos = 2)
    private String country;
    @DataField(pos = 3)
    private String itemType;
    @DataField(pos = 4)
    private String salesChannel;
    @DataField(pos = 5)
    private String orderPriority;
    @DataField(pos = 6, pattern = "MM/dd/yyyy")
    private Date orderDate;
    @DataField(pos = 7)
    private Long orderId;
    @DataField(pos = 8, pattern = "MM/dd/yyyy")
    private Date shipDate;
    @DataField(pos = 9)
    private Integer unitsSold;
    @DataField(pos = 10, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal unitPrice;
    @DataField(pos = 11, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal unitCost;
    @DataField(pos = 12, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalRevenue;
    @DataField(pos = 13, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalCost;
    @DataField(pos = 14, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalProfit;

}
