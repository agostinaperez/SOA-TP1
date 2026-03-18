package com.soa.tp1.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private long price;
    private int quantity;
    public enum Currency { USD, ARS, EUR }
    
}
