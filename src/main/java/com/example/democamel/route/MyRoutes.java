package com.example.democamel.route;

import com.example.democamel.handler.HandleOrders;
import com.example.democamel.handler.HandleRegionReportTable;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import com.example.democamel.processor.ProcessorAveragesAndTotals;
import com.example.democamel.processor.ProcessorHeaders;
import com.example.democamel.strategy.AggregateCountries;
import com.example.democamel.strategy.AggregateRegions;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyRoutes extends RouteBuilder {

    @Override
    public void configure() {

        from("file:in?noop=true")
                .unmarshal()
                .bindy(BindyType.Csv, OrderFromCsv.class)
                .split(body())
                .log(body().toString())
                .multicast()
                .to("direct:onlineOrdersToDb", "direct:aggregateRegionReports");

        from("direct:onlineOrdersToDb")
                .filter().method(HandleOrders.class, "checkIfOrderOnline")
                .bean(HandleOrders.class, "addOnlineOrdersToDb");

        from("direct:aggregateRegionReports")
                .process(new ProcessorHeaders())
                .aggregate(header("country"), new AggregateCountries())
                .completionTimeout(500)
                .process(new ProcessorAveragesAndTotals())
                .aggregate(header("region"), new AggregateRegions())
                .completionTimeout(500)
                .process(exchange -> {
                    String region = exchange.getIn().getHeader("region", String.class);
                    List<OrderReportCsv> OrderReportCsvList = exchange.getIn().getBody(OrdersAggregatedByRegion.class).getAggregatedRegions().get(region);
                    exchange.getIn().setBody(OrderReportCsvList);
                })
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd HH-mm-ss}");


        from("file:output/reports?noop=true")
                .unmarshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .split(body())
                .bean(HandleRegionReportTable.class, "addToRegionReportTable");
    }

}
