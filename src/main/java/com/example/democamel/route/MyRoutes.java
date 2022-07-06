package com.example.democamel.route;

import com.example.democamel.MyStrategy;
import com.example.democamel.model.OrderFromCsv;
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
//                .csv()
                .bindy(BindyType.Csv, OrderFromCsv.class)
                .split(body(), new MyStrategy())
//                .log(body().toString())
                .to("mock:out");

    }

}