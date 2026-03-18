package com.soa.tp1.order.api.model;

import java.util.ArrayList;
import java.util.Currency;
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
    private long id;
    private long amount;
    private Currency currency;
    private Date creationDate;
    private List<Item> items = new ArrayList<>();
}
