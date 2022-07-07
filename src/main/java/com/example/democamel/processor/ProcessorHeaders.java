package com.example.democamel.processor;

import com.example.democamel.model.OrderFromCsv;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessorHeaders implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String region = exchange.getIn().getBody(OrderFromCsv.class).getRegion();
        String country = exchange.getIn().getBody(OrderFromCsv.class).getCountry();
        exchange.getIn().setHeader("region",region);
        exchange.getIn().setHeader("country",country);
    }
}
