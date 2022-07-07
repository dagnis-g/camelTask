package com.example.democamel.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class OrdersAggregatedByCountry {

    private Map<String, OrderTotals> countryToOrderTotals = new HashMap<>();
}
