package com.example.democamel;

import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrderTotals;
import com.example.democamel.model.OrdersAggregatedByRegion;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

@Slf4j
public class MyStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange previous, Exchange current) {
        if (previous == null) {
            var ordersAggregatedByRegion = new OrdersAggregatedByRegion();

            var orderFromCsv = current.getIn().getBody(OrderFromCsv.class);
            ordersAggregatedByRegion.getRegionToOrderTotals().put(
                    orderFromCsv.getRegion(),
                    new OrderTotals(orderFromCsv.getUnitsSold()));

            current.getIn().setHeader("unitsSold",orderFromCsv.getUnitsSold());

            current.getIn().setBody(ordersAggregatedByRegion, OrdersAggregatedByRegion.class);
            return current;
        }

        var ordersAggregatedByRegion = previous.getIn().getBody(OrdersAggregatedByRegion.class);
        var orderFromCsv = current.getIn().getBody(OrderFromCsv.class);

        Integer prevUnitsSold = previous.getIn().getHeader("unitsSold",Integer.class);

        ordersAggregatedByRegion.getRegionToOrderTotals().put( // todo add, instead of just putting
                orderFromCsv.getRegion(),
                new OrderTotals(orderFromCsv.getUnitsSold()+prevUnitsSold));
        previous.getIn().setHeader("unitsSold",orderFromCsv.getUnitsSold()+prevUnitsSold);

        return previous;
    }
}
