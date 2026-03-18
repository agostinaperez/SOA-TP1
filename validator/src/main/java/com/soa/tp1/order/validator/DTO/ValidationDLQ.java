package com.soa.tp1.order.validator.DTO;

import lombok.Data;

//mensaje que se envía a la Dead Letter Queue si la validación falla.
@Data
public class ValidationDLQ {

    private Integer orderId;
    private String reason;

}