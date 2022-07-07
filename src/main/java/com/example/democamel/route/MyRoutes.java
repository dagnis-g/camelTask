package com.example.democamel.route;

import com.example.democamel.processor.ProcessorHeaders;
import com.example.democamel.processor.ProcessorRegions;
import com.example.democamel.strategy.AggregateCountries;
import com.example.democamel.strategy.AggregateRegions;
import com.example.democamel.model.OrderForCsv;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrderTotals;
import com.example.democamel.model.OrdersAggregatedByCountry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
                .aggregate(header("country"),new AggregateCountries())
                .completionTimeout(500)
                .process(new ProcessorRegions())
                .log(body().toString())
                .aggregate(header("region"), new AggregateRegions())
                .completionTimeout(500)
                .log(body().toString())
                .to("mock:out");

    }

}
