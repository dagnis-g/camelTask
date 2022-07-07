package com.example.democamel.strategy;

import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrderTotals;
import com.example.democamel.model.OrdersAggregatedByCountry;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.math.BigDecimal;

@Slf4j
public class AggregateCountries implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange previous, Exchange current) {
        if (previous == null) {
            var ordersAggregatedByCountry = new OrdersAggregatedByCountry();
            var orderFromCsv = current.getIn().getBody(OrderFromCsv.class);

            String region = orderFromCsv.getRegion();
            String country = orderFromCsv.getCountry();
            BigDecimal unitsSold = orderFromCsv.getUnitsSold();
            BigDecimal unitPrice = orderFromCsv.getUnitPrice();
            BigDecimal unitCost = orderFromCsv.getUnitCost();

            ordersAggregatedByCountry.getCountryToOrderTotals().put(
                    country,
                    new OrderTotals(region,country, unitsSold,unitPrice,unitCost,BigDecimal.valueOf(1)));

            current.getIn().setBody(ordersAggregatedByCountry, OrdersAggregatedByCountry.class);
            return current;
        }

        var ordersAggregatedByCountry = previous.getIn().getBody(OrdersAggregatedByCountry.class);
        var orderFromCsv = current.getIn().getBody(OrderFromCsv.class);

        String region = orderFromCsv.getRegion();
        String country = orderFromCsv.getCountry();
        BigDecimal unitsSold = orderFromCsv.getUnitsSold();
        BigDecimal unitPrice = orderFromCsv.getUnitPrice();
        BigDecimal unitCost = orderFromCsv.getUnitCost();

        OrderTotals prevOrder = ordersAggregatedByCountry.getCountryToOrderTotals().getOrDefault(
                country,
                new OrderTotals(
                        region,country,BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(1)));

        BigDecimal unitsSoldTotal = prevOrder.getUnitsSoldTotal();
        BigDecimal unitsPriceTotal = prevOrder.getUnitsPriceTotal();
        BigDecimal unitsCostTotal = prevOrder.getUnitCostTotal();
        BigDecimal ordersTotal = prevOrder.getTotalOrders();

        prevOrder.setUnitsSoldTotal(unitsSoldTotal.add(unitsSold));
        prevOrder.setUnitsPriceTotal(unitsPriceTotal.add(unitPrice));
        prevOrder.setUnitCostTotal(unitsCostTotal.add(unitCost));
        prevOrder.setTotalOrders(ordersTotal.add(BigDecimal.valueOf(1)));

        ordersAggregatedByCountry.getCountryToOrderTotals().putIfAbsent(country, prevOrder);

        return previous;
    }
}
