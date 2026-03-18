package com.soa.tp1.order.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    private long orderId;
    private long amount;
    private String currency;
    private Date creationDate;
    private List<Item> items = new ArrayList<>();
}
