package com.example.democamel.processor;

import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;

public class ProcessRegionAggregateToList implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String region = exchange.getIn().getHeader("region", String.class);
        List<OrderReportCsv> OrderReportCsvList = exchange.getIn().getBody(OrdersAggregatedByRegion.class).getAggregatedRegions().get(region);
        exchange.getIn().setBody(OrderReportCsvList);
    }
}
