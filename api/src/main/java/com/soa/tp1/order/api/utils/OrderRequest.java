package com.soa.tp1.order.api.utils;

import java.util.List;

import com.soa.tp1.order.api.model.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private long amount;
    private List<Item> items;
}