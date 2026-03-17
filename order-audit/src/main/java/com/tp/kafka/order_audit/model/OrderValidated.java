package com.tp.kafka.order_audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidated {
    private String orderId;
    private double amount;
    private String currency;
}