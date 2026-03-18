package com.soa.tp1.order.validator.consumer;

import com.soa.tp1.order.validator.DTO.OrderCreated;
import com.soa.tp1.order.validator.DTO.OrderValidated;
import com.soa.tp1.order.validator.DTO.ValidationDLQ;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderValidatorConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderValidatorConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "orders.created.v1")
    public void validate(OrderCreated order) {

        System.out.println("Orden recibida: " + order.getOrderId());

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

            System.out.println("Orden válida, enviada a orders.validated.v1");

        }else{

            ValidationDLQ dlq = new ValidationDLQ();
            dlq.setOrderId(order.getOrderId());
            dlq.setReason("Validation failed");

            kafkaTemplate.send(
                    "orders.validation.dlq.v1",
                    order.getOrderId().toString(),
                    dlq
            );

            System.out.println("Orden inválida, enviada a DLQ");
        }
    }
}