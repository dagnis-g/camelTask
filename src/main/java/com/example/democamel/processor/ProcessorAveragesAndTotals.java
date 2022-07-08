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

        OrderReportCsv orderReportCsv = new OrderReportCsv();
        orderReportCsv.setCountry(orderTotals.getCountry());
        orderReportCsv.setOrderCount(totalOrders);
        orderReportCsv.setAverageUnitsSold(soldTotal.divide(totalOrders, RoundingMode.HALF_UP));
        orderReportCsv.setAverageUnitPrice(priceTotal.divide(totalOrders, RoundingMode.HALF_UP));
        orderReportCsv.setAverageUnitCost(costTotal.divide(totalOrders, RoundingMode.HALF_UP));
//        orderForCsv.setTotalRevenue(priceTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));
//        orderForCsv.setTotalCost(costTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));
//        orderForCsv.setTotalProfit(soldTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));
        orderReportCsv.setTotalRevenue(priceTotal);
        orderReportCsv.setTotalCost(costTotal);
        orderReportCsv.setTotalProfit(soldTotal);

        exchange.getIn().setBody(orderReportCsv, OrderReportCsv.class);

    }
}
