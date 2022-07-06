package com.example.democamel;

import com.example.democamel.model.OrderFromCsv;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.Map;


public class MyStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange previous, Exchange current) {

        //TODO
        ///From Csv to FilterOnline to here to RegionReport table and RegionReport Csv
        //  region:     country:   columnName-value(RegionReport)
        //    |            |           |       |
        //    v            v           v       v
        Map<String, Map<String, Map<String, String>>> mapRegionMapCountry;


//                                                                if(fileByRegionNameDoesntExist){
//                                                                    createFile();
//                                                                        if(rowByCountryNameDoesntExist){
//                                                                            createRow saveDataInRow();
//                                                                        } else {
//                                                                            readAndUpdateDataInRow();
//                                                                        }
//                                                                else{
//                                                                   findFile openFile FindRow ReadAndUpdateData();
//                                                                    }
//                                                                }

        if (previous == null) {

            OrderFromCsv curr = (OrderFromCsv) current.getIn().getBody();
            curr.setOrderId(1L);
            System.out.println(curr.getOrderId());
            return current;

        } else {

            OrderFromCsv prev = (OrderFromCsv) previous.getIn().getBody();
            prev.setOrderId(prev.getOrderId() + 1);
            System.out.println(prev.getOrderId());
            return previous;

        }
    }
}
