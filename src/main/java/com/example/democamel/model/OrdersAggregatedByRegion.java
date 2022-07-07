package com.example.democamel.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class OrdersAggregatedByRegion {

    private Map<String, List< OrderForCsv>> aggregatedRegions = new HashMap<>();

}
