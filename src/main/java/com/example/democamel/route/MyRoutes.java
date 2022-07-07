package com.example.democamel.route;

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
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String region = exchange.getIn().getBody(OrderFromCsv.class).getRegion();
                        String country = exchange.getIn().getBody(OrderFromCsv.class).getCountry();
                        exchange.getIn().setHeader("region",region);
                        exchange.getIn().setHeader("country",country);
                    }
                })
                .aggregate(header("country"),new AggregateCountries())
                .completionTimeout(500)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        OrdersAggregatedByCountry orderByCountry = exchange.getIn().getBody(OrdersAggregatedByCountry.class);
                        String country = exchange.getIn().getHeader("country",String.class);
                        OrderTotals  orderTotals = orderByCountry.getCountryToOrderTotals().get(country);
                        BigDecimal soldTotal = orderTotals.getUnitsSoldTotal();
                        BigDecimal priceTotal = orderTotals.getUnitsPriceTotal();
                        BigDecimal costTotal = orderTotals.getUnitCostTotal();
                        BigDecimal totalOrders = orderTotals.getTotalOrders();

                        OrderForCsv orderForCsv = new OrderForCsv();
                        orderForCsv.setCountry(orderTotals.getCountry());
                        orderForCsv.setOrderCount(totalOrders);
                        orderForCsv.setAverageUnitsSold(soldTotal.divide(totalOrders,RoundingMode.HALF_UP));
                        orderForCsv.setAverageUnitPrice(priceTotal.divide(totalOrders,RoundingMode.HALF_UP));
                        orderForCsv.setAverageUnitCost(costTotal.divide(totalOrders,RoundingMode.HALF_UP));
                        orderForCsv.setTotalRevenue(priceTotal.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP));
                        orderForCsv.setTotalCost(costTotal.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP));
                        orderForCsv.setTotalProfit(soldTotal.divide(BigDecimal.valueOf(1000000),RoundingMode.HALF_UP));

                        exchange.getIn().setBody(orderForCsv,OrderForCsv.class);

                    }
                })
                .aggregate(header("region"), new AggregateRegions())
                .completionTimeout(500)
                .log(body().toString())
                .to("mock:out");

    }

}
