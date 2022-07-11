package com.example.democamel.handler;

import com.example.democamel.model.OrderEntity;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.repo.OrdersOnlineRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.modelmapper.ModelMapper;

@AllArgsConstructor
public class HandleOrders {

    private final OrdersOnlineRepository ordersOnlineRepository;
    private final ModelMapper modelMapper;

//    public boolean checkIfOrderOnline(OrderFromCsv body) {
//        return body.getSalesChannel().equalsIgnoreCase("online");
//    }

    public static boolean checkIfOrderOnline(Exchange exchange) {
        return exchange.getIn().getBody(OrderFromCsv.class).getSalesChannel().equalsIgnoreCase("online");
    }

//    public void addOnlineOrdersToDb(OrderFromCsv body) {
//        OrderEntity orderEntity = modelMapper.map(body, OrderEntity.class);
//        ordersOnlineRepository.save(orderEntity);
//    }

    public void addOnlineOrdersToDb(Exchange exchange) {
        OrderEntity orderEntity = modelMapper.map(exchange.getIn().getBody(OrderFromCsv.class), OrderEntity.class);
        ordersOnlineRepository.save(orderEntity);
    }

}
