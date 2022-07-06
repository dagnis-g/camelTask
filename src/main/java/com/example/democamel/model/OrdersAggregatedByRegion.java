package com.example.democamel.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

// todo possibly can rename to something better
@Data
public class OrdersAggregatedByRegion {

    private Map<String, OrderTotals> regionToOrderTotals = new HashMap<>();
}
