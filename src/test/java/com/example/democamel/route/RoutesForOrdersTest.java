package com.example.democamel.route;

import com.example.democamel.model.OrderEntity;
import com.example.democamel.model.OrderFromCsv;
import com.example.democamel.model.RegionReport;
import com.example.democamel.repo.OrdersOnlineRepository;
import com.example.democamel.repo.RegionReportRepository;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;


@SpringBootTest
@CamelSpringBootTest
@MockEndpoints("direct:onlineOrdersToDb")
class RoutesForOrdersTest {
    @Autowired
    private ProducerTemplate template;

    @EndpointInject("mock:direct:onlineOrdersToDb")
    private MockEndpoint mock;

    @EndpointInject("mock:file:output/reports?fileName=${header.region}_${date:now:yyyy-MM-dd HH-mm-ss}")
    private MockEndpoint mockToCsv;

    @Autowired
    OrdersOnlineRepository onlineRepository;

    @Autowired
    RegionReportRepository reportRepository;

    @AfterEach
    void clearRepo() {
        onlineRepository.deleteAll();
        reportRepository.deleteAll();

    }

    @Test
    void shouldUnmarshalToOrderFromCsvAndSkipColumnHeaders() throws InterruptedException {
        mock.expectedBodiesReceived("OrderFromCsv(region=Australia and Oceania, country=Australia, itemType=Meat, salesChannel=Online, orderPriority=C, orderDate=Mon Apr 04 00:00:00 EEST 2011, orderId=451691138, shipDate=Mon May 23 00:00:00 EEST 2011, unitsSold=4300, unitPrice=421.89, unitCost=364.69, totalRevenue=1814127.00, totalCost=1568167.00, totalProfit=245960.00)");
        template.sendBody("file:in", "Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit\n" +
                "Australia and Oceania,Australia,Meat,Online,C,4/4/2011,451691138,5/23/2011,4300,421.89,364.69,1814127.00,1568167.00,245960.00");
        mock.assertIsSatisfied();
    }

    @Test
    void shouldAddOnlineOrderToDb() {
        OrderFromCsv order = new OrderFromCsv("Australia and Oceania",
                "Australia",
                "Meat",
                "Online",
                "asd",
                Date.from(Instant.now()),
                451691138L,
                Date.from(Instant.now()),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L)
        );
        template.sendBody("direct:onlineOrdersToDb", order);
        List<OrderEntity> onlineOrders = onlineRepository.findAll();
        Assertions.assertEquals(1, onlineOrders.size());
    }

    @Test
    void shouldNotAddOfflineOrderToDb() {
        OrderFromCsv order = new OrderFromCsv("Australia and Oceania",
                "Australia",
                "Meat",
                "Offline",
                "asd",
                Date.from(Instant.now()),
                451691138L,
                Date.from(Instant.now()),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L)
        );
        template.sendBody("direct:onlineOrdersToDb", order);
        List<OrderEntity> onlineOrders = onlineRepository.findAll();
        Assertions.assertEquals(0, onlineOrders.size());
    }

    @Test
    void shouldAddRegionAndCountryHeader() throws InterruptedException {
        OrderFromCsv order = new OrderFromCsv("Australia and Oceania",
                "Australia",
                "Meat",
                "Offline",
                "asd",
                Date.from(Instant.now()),
                451691138L,
                Date.from(Instant.now()),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L),
                BigDecimal.valueOf(4300L)
        );
        template.sendBody("direct:aggregateRegionReports", order);
        mockToCsv.expectedHeaderReceived("region1", "Australia and Oceania");
        mockToCsv.expectedHeaderReceived("country", "Australia");
        mockToCsv.expectedBodiesReceived("ds");
        mockToCsv.assertIsSatisfied();
    }

    @Test
    void shouldAddToRegionReportTable() {
        template.sendBody("file:output/reports?noop=true",
                "country,orderCount,averageUnitsSold,averageUnitPrice,averageUnitCost,totalRevenue,totalCost,totalProfit\n" +
                        "Kyrgyzstan,1,124,154.06,90.93,0.02,0.01,0.01\n" +
                        "Bangladesh,1,8263,109.28,35.84,0.90,0.30,0.61\n" +
                        "Mongolia,1,4901,81.73,56.67,0.40,0.28,0.12\n" +
                        "Sri Lanka,1,6952,437.20,263.33,3.04,1.83,1.21\n" +
                        "Brunei,1,6708,651.21,524.96,4.37,3.52,0.85\n" +
                        "Myanmar,2,7090,388.78,269.19,6.16,4.36,1.80\n" +
                        "Turkmenistan,2,4420,659.74,513.75,5.82,4.55,1.27\n" +
                        "Laos,1,3732,154.06,90.93,0.57,0.34,0.24\n" +
                        "Malaysia,1,6267,9.33,6.92,0.06,0.04,0.02");
        List<RegionReport> orders = reportRepository.findAll();
        Assertions.assertEquals(9, orders.size());
    }

}