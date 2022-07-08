package com.example.democamel.strategy;

import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.ArrayList;

public class AggregateRegions implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange previous, Exchange current) {
        if (previous == null) {
            OrdersAggregatedByRegion ordersAggregatedByRegion = new OrdersAggregatedByRegion();
            OrderReportCsv orderReportCsv = current.getIn().getBody(OrderReportCsv.class);
            String region = current.getIn().getHeader("region", String.class);

            ordersAggregatedByRegion.getAggregatedRegions().computeIfAbsent(region, k -> new ArrayList<>()).add(orderReportCsv);
            current.getIn().setBody(ordersAggregatedByRegion, OrdersAggregatedByRegion.class);

            return current;
        }

        OrdersAggregatedByRegion ordersAggregatedByRegion = previous.getIn().getBody(OrdersAggregatedByRegion.class);
        OrderReportCsv orderReportCsv = current.getIn().getBody(OrderReportCsv.class);
        String region = current.getIn().getHeader("region", String.class);

        ordersAggregatedByRegion.getAggregatedRegions().computeIfAbsent(region, k -> new ArrayList<>()).add(orderReportCsv);

        return previous;

    }
}
