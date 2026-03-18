package com.soa.tp1.order.validator.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.soa.tp1.order.validator.DTO.OrderCreated;
import com.soa.tp1.order.validator.DTO.OrderValidated;
import com.soa.tp1.order.validator.DTO.ValidationDLQ;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
@Slf4j
@Component
public class OrderValidatorConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderValidatorConsumer(KafkaTemplate<String, Object> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "orders.created.v1")
    public void validate(String message) {  // <-- recibís String
        OrderCreated order;
        try {
            order = objectMapper.readValue(message, OrderCreated.class);
        } catch (Exception e) {
            log.error("Error deserializando orden: " + e.getMessage());
            return;
        }

        log.info("Orden recibida: " + order.getOrderId());
        boolean valid =
                order.getAmount() != null &&
                order.getAmount() > 0 &&
                List.of("ARS","USD","EUR").contains(order.getCurrency()) &&
                order.getItems() != null &&
                !order.getItems().isEmpty()&&
                order.getItems().stream().allMatch(i -> i.getQuantity() > 0);
        
        if(valid){

            OrderValidated validated = new OrderValidated();
            validated.setOrderId(order.getOrderId());
            validated.setAmount(order.getAmount());
            validated.setCurrency(order.getCurrency());

            kafkaTemplate.send(
                    "orders.validated.v1",
                    order.getOrderId().toString(),
                    validated
            );

            log.info("Orden válida, enviada a orders.validated.v1");

        }else{

            ValidationDLQ dlq = new ValidationDLQ();
            dlq.setOrderId(order.getOrderId());
            dlq.setReason("Validation failed");

            kafkaTemplate.send(
                    "orders.validation.dlq.v1",
                    order.getOrderId().toString(),
                    dlq
            );

            log.info("Orden inválida, enviada a DLQ");
        }
    }
}