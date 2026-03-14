package com.soa.tp1.order.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soa.tp1.order.api.model.Order;
import com.soa.tp1.order.api.model.business.interfaces.IOrderBusiness;
import com.soa.tp1.order.api.utils.OrderRequest;

@RestController
@RequestMapping(Constants.URL_ORDEN)
public class OrdenController {
    @Autowired
    private IOrderBusiness orderBusiness;

    @PostMapping(value ="")
    public ResponseEntity<Order> add(@RequestBody OrderRequest request) {
        Order order = orderBusiness.processOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
