package com.example.democamel.strategy;

import com.example.democamel.model.OrderForCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.List;

public class AggregateRegions implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange previous, Exchange current) {
        if (previous == null){
            OrdersAggregatedByRegion ordersAggregatedByRegion = new OrdersAggregatedByRegion();
            OrderForCsv orderForCsv = current.getIn().getBody(OrderForCsv.class);
            String region = current.getIn().getHeader("region",String.class);

            ordersAggregatedByRegion.getAggregatedRegions().put(region, List.of(orderForCsv));
            current.getIn().setBody(ordersAggregatedByRegion,OrdersAggregatedByRegion.class);

            return current;
        }

        OrdersAggregatedByRegion ordersAggregatedByRegion = previous.getIn().getBody(OrdersAggregatedByRegion.class);
        OrderForCsv orderForCsv = current.getIn().getBody(OrderForCsv.class);

        String region = current.getIn().getHeader("region",String.class);

        List<OrderForCsv> ordersForCsv = ordersAggregatedByRegion.getAggregatedRegions().getOrDefault(region,List.of(orderForCsv));
        ordersForCsv.add(orderForCsv);
        ordersAggregatedByRegion.getAggregatedRegions().putIfAbsent(region,ordersForCsv);

        return previous;
    }
}
