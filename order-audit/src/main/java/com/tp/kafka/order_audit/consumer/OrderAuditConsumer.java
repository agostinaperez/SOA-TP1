package com.tp.kafka.order_audit.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.tp.kafka.order_audit.model.OrderValidated;


@Service
public class OrderAuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderAuditConsumer.class);

    @KafkaListener(
            topics = "orders.validated.v1"
    )
    public void consume(
            OrderValidated event,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {

        log.info("AUDIT EVENT RECEIVED -> {}", event);
        log.info("METADATA -> partition={}, offset={}", partition, offset);
    }
}