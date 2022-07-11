package com.example.democamel.handler;

import com.example.democamel.helper.ParseDateTimeFromFile;
import com.example.democamel.model.OrderReportCsv;
import com.example.democamel.model.RegionReport;
import com.example.democamel.repo.RegionReportRepository;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@AllArgsConstructor
public class HandleRegionReportTable {

    private final ModelMapper modelMapper;
    private final RegionReportRepository regionReportRepository;

    public void addToRegionReportTable(Exchange exchange) throws Exception {
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
}
