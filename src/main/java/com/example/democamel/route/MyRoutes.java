package com.example.democamel.route;

import com.example.democamel.model.OrderForCsv;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import com.example.democamel.processor.ProcessorAveragesAndTotals;
import com.example.democamel.processor.ProcessorHeaders;
import com.example.democamel.strategy.AggregateCountries;
import com.example.democamel.strategy.AggregateRegions;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyRoutes extends RouteBuilder {

    @Override
    public void configure() {

//        from("file-watch:in")
//                .unmarshal()
//                .bindy(BindyType.Csv, OrderFromCsv.class)
//                .split(body())
//                .filter().method(HandleOrders.class, "checkIfOrderOnline")
//                .log("${body}");
//
//        from("file-watch:in")
//                .unmarshal()
//                .bindy(BindyType.Csv, OrderFromCsv.class)
//                .split(body())
//                .filter().method(HandleOrders.class, "checkIfOrderOnline")
//                .bean(HandleOrders.class, "addOnlineOrdersToDb")
//                .log(body().toString());

        from("file-watch:in")
                .unmarshal()
                .bindy(BindyType.Csv, OrderFromCsv.class)
                .split(body())
                .process(new ProcessorHeaders())
                .aggregate(header("country"), new AggregateCountries())
                .completionTimeout(500)
                .process(new ProcessorAveragesAndTotals())
                .aggregate(header("region"), new AggregateRegions())
                .completionTimeout(500)
//                .log(body().toString())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String region = exchange.getIn().getHeader("region", String.class);
                        List<OrderForCsv> orderForCsvList = exchange.getIn().getBody(OrdersAggregatedByRegion.class).getAggregatedRegions().get(region);
                        exchange.getIn().setBody(orderForCsvList);
                    }
                })
                .log(body().toString())
                .choice()
                .when(simple("${header.region} == 'Asia'"))
//                .split(body())
                .marshal().csv()
//                .marshal().bindy(BindyType.Csv, OrderForCsv.class)
                .to("file:output/asia");

//                .otherwise()
//                .to("file:output");
//                .to("mock:out");
    }

}
