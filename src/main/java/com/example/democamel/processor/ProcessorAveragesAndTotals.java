package com.example.democamel.processor;

import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.OrderTotals;
import com.example.democamel.model.OrdersAggregatedByCountry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProcessorAveragesAndTotals implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        OrdersAggregatedByCountry orderByCountry = exchange.getIn().getBody(OrdersAggregatedByCountry.class);
        String country = exchange.getIn().getHeader("country", String.class);
        OrderTotals orderTotals = orderByCountry.getCountryToOrderTotals().get(country);
        BigDecimal soldTotal = orderTotals.getUnitsSoldTotal();
        BigDecimal priceTotal = orderTotals.getUnitsPriceTotal();
        BigDecimal costTotal = orderTotals.getUnitCostTotal();
        BigDecimal totalOrders = orderTotals.getTotalOrders();

        BigDecimal ONE_MILLION = BigDecimal.valueOf(1000000L);
        BigDecimal avgSold = soldTotal.divide(totalOrders, RoundingMode.HALF_UP);
        BigDecimal avgPrice = priceTotal.divide(totalOrders, RoundingMode.HALF_UP);
        BigDecimal avgCost = costTotal.divide(totalOrders, RoundingMode.HALF_UP);
        BigDecimal totalRevenueInMil = orderTotals.getTotalRevenue().divide(ONE_MILLION, RoundingMode.HALF_UP);
        BigDecimal totalCostInMil = orderTotals.getTotalCost().divide(ONE_MILLION, RoundingMode.HALF_UP);
        BigDecimal totalProfitInMil = orderTotals.getTotalProfit().divide(ONE_MILLION, RoundingMode.HALF_UP);


        OrderReportCsv orderReportCsv = new OrderReportCsv(country,
                totalOrders,
                avgSold,
                avgPrice,
                avgCost,
                totalRevenueInMil,
                totalCostInMil,
                totalProfitInMil);

        exchange.getIn().setBody(orderReportCsv, OrderReportCsv.class);

    }
}
