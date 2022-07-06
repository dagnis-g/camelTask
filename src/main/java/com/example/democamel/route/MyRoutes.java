package com.example.democamel.route;

import com.example.democamel.MyStrategy;
import com.example.democamel.model.OrderFromCsv;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

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
                        exchange.getIn().setHeader("region",region);
                    }
                })
                .aggregate(header("region"),new MyStrategy())
//                .constant(true)
                .completionInterval(500)
                .log(body().toString())
                .to("mock:out");

    }

}
