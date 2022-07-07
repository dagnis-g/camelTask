package com.example.democamel.processor;

import com.example.democamel.model.OrderForCsv;
import com.example.democamel.model.OrderTotals;
import com.example.democamel.model.OrdersAggregatedByCountry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProcessorRegions implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        OrdersAggregatedByCountry orderByCountry = exchange.getIn().getBody(OrdersAggregatedByCountry.class);
        String country = exchange.getIn().getHeader("country", String.class);
        OrderTotals orderTotals = orderByCountry.getCountryToOrderTotals().get(country);
        BigDecimal soldTotal = orderTotals.getUnitsSoldTotal();
        BigDecimal priceTotal = orderTotals.getUnitsPriceTotal();
        BigDecimal costTotal = orderTotals.getUnitCostTotal();
        BigDecimal totalOrders = orderTotals.getTotalOrders();

        OrderForCsv orderForCsv = new OrderForCsv();
        orderForCsv.setCountry(orderTotals.getCountry());
        orderForCsv.setOrderCount(totalOrders);
        orderForCsv.setAverageUnitsSold(soldTotal.divide(totalOrders, RoundingMode.HALF_UP));
        orderForCsv.setAverageUnitPrice(priceTotal.divide(totalOrders, RoundingMode.HALF_UP));
        orderForCsv.setAverageUnitCost(costTotal.divide(totalOrders, RoundingMode.HALF_UP));
        orderForCsv.setTotalRevenue(priceTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));
        orderForCsv.setTotalCost(costTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));
        orderForCsv.setTotalProfit(soldTotal.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP));

        exchange.getIn().setBody(orderForCsv, OrderForCsv.class);

    }
}
