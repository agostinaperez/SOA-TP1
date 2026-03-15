package com.soa.tp1.order.validator.DTO;

import lombok.Data;

//item dentro de una orden
@Data
public class OrderItem {

    private Integer itemId;
    private Integer quantity;

}
