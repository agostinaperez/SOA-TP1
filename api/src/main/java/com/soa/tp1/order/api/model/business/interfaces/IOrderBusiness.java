package com.soa.tp1.order.api.model.business.interfaces;

import com.soa.tp1.order.api.model.Order;
import com.soa.tp1.order.api.utils.OrderRequest;

public interface IOrderBusiness {
    public Order processOrder(OrderRequest request);
}
