package com.example.democamel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CsvRecord(separator = ",", skipFirstLine = true, generateHeaderColumns = true)
public class OrderReportCsv {

    @DataField(pos = 1)
    private String country;
    @DataField(pos = 2, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal orderCount;
    @DataField(pos = 3, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal averageUnitsSold;
    @DataField(pos = 4, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal averageUnitPrice;
    @DataField(pos = 5, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal averageUnitCost;
    @DataField(pos = 6, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalRevenue;
    @DataField(pos = 7, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalCost;
    @DataField(pos = 8, trim = true, precision = 2, pattern = "00.00")
    private BigDecimal totalProfit;

}
