package com.example.democamel.handler;

import com.example.democamel.model.OrderEntity;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.repo.OrdersOnlineRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("handleOrders")
@AllArgsConstructor
public class HandleOrders {

    private final OrdersOnlineRepository ordersOnlineRepository;
    private final ModelMapper modelMapper;

    public boolean checkIfOrderOnline(OrderFromCsv body) {
        return body.getSalesChannel().equalsIgnoreCase("online");
    }

    public void addOnlineOrdersToDb(OrderFromCsv body) {
        OrderEntity orderEntity = modelMapper.map(body, OrderEntity.class);
        ordersOnlineRepository.save(orderEntity);
    }


}
