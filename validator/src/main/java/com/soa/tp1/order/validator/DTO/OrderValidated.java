package com.soa.tp1.order.validator.DTO;

import lombok.Data;

//evento que se va a producir si la orden es válida.
@Data
public class OrderValidated {

    private Integer orderId;
    private Double amount;
    private String currency;

}