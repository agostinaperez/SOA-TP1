package com.soa.tp1.order.validator.DTO;

import lombok.Data;
import java.util.List;

//evento que consume el servicio desde el topic: orders.created.v1
@Data
public class OrderCreated {

    private Integer orderId;
    private Double amount;
    private String currency;
    private String creationDate;
    private List<OrderItem> items;

}
