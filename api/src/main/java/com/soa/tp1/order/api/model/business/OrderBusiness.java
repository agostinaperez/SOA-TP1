package com.soa.tp1.order.api.model.business;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.soa.tp1.order.api.model.Order;
import com.soa.tp1.order.api.model.business.interfaces.IOrderBusiness;
import com.soa.tp1.order.api.utils.OrderRequest;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {

    private static final String TOPIC = "orders.created.v1";
    private static final AtomicLong idSequence = new AtomicLong(1);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Order processOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId(idSequence.getAndIncrement());
        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setCreationDate(new Date());
        order.setItems(request.getItems());
        try {
                String payload = objectMapper.writeValueAsString(order);
                kafkaTemplate.send(TOPIC, String.valueOf(order.getOrderId()), payload);
                log.info("Orden publicada en topic '{}': {}", TOPIC, payload);
        } catch (Exception e) {
                log.error("Error serializando orden: {}", e.getMessage());
                throw new RuntimeException("Error al publicar la orden", e);
        }

    

        return order;
    }
}