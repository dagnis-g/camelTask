package com.example.democamel.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class OrderEntity {

    @Id
    private Long orderId;
    private Date orderDate;
    private String orderPriority;
    private Date shipDate;
    private BigDecimal unitCost;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private String itemType;
    private String country;
    
}
