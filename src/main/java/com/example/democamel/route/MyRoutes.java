package com.example.democamel.route;

import com.example.democamel.handler.HandleOrders;
import com.example.democamel.helper.ParseDateTimeFromFile;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.OrdersAggregatedByRegion;
import com.example.democamel.model.RegionReport;
import com.example.democamel.processor.ProcessorAveragesAndTotals;
import com.example.democamel.processor.ProcessorHeaders;
import com.example.democamel.repo.RegionReportRepository;
import com.example.democamel.strategy.AggregateCountries;
import com.example.democamel.strategy.AggregateRegions;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class MyRoutes extends RouteBuilder {

    private final ModelMapper modelMapper;
    private final RegionReportRepository regionReportRepository;

    @Override
    public void configure() {
        
        from("file:in?noop=true")
                .unmarshal()
                .bindy(BindyType.Csv, OrderFromCsv.class)
                .split(body())
                .multicast()
                .to("direct:addOnlineOrdersToDb", "direct:regionReportsToCsv");

        from("direct:addOnlineOrdersToDb")
                .filter().method(HandleOrders.class, "checkIfOrderOnline")
                .bean(HandleOrders.class, "addOnlineOrdersToDb")
                .log(body().toString());

        from("direct:regionReportsToCsv")
                .process(new ProcessorHeaders())
                .aggregate(header("country"), new AggregateCountries())
                .completionTimeout(500)
                .process(new ProcessorAveragesAndTotals())
                .aggregate(header("region"), new AggregateRegions())
                .completionTimeout(500)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String region = exchange.getIn().getHeader("region", String.class);
                        List<OrderReportCsv> OrderReportCsvList = exchange.getIn().getBody(OrdersAggregatedByRegion.class).getAggregatedRegions().get(region);
                        exchange.getIn().setBody(OrderReportCsvList);
                    }
                })
                .to("direct:toCsvFiles");

        from("direct:toCsvFiles")
                .log(body().toString())
                .choice()
                .when(simple("${header.region} == 'Asia'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'Australia and Oceania'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'Central America and the Caribbean'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'Europe'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'Middle East and North Africa'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'North America'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice()
                .when(simple("${header.region} == 'Sub-Saharan Africa'"))
                .marshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .to("file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd hh-mm-ss}")
                .endChoice();


        from("file:output/reports?noop=true")
                .unmarshal().bindy(BindyType.Csv, OrderReportCsv.class)
                .split(body())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        ParseDateTimeFromFile parseDateTimeFromFile = new ParseDateTimeFromFile();
                        String filenameHeader = exchange.getIn().getHeader("CamelFileNameOnly", String.class);
                        String[] splitFileNameHeader = filenameHeader.split("_");
                        String region = splitFileNameHeader[0];
                        LocalDateTime dateTime = parseDateTimeFromFile.parse(splitFileNameHeader[1]);

                        OrderReportCsv orderReportCsv = exchange.getIn().getBody(OrderReportCsv.class);

                        RegionReport regionReport = modelMapper.map(orderReportCsv, RegionReport.class);
                        regionReport.setRegion(region);
                        regionReport.setCsvFileName(filenameHeader);
                        regionReport.setProcessingDate(dateTime);

                        regionReportRepository.save(regionReport);
                    }
                })
                .log(body().toString());


    }

}
