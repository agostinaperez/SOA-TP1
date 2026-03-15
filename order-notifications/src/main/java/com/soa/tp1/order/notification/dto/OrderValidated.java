package com.soa.tp1.order.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
//este es el el evento que consumo acá en el microservicio de notificaciones, 
// es el mismo que se publica en el microservicio de ordenes

public record OrderValidated(
    @JsonProperty("orderId") String orderId,
    @JsonProperty("amount") double amount,
    @JsonProperty("creationDate") LocalDateTime creationDate,
    @JsonProperty("items") List<String> items
) {
    @JsonCreator
    public OrderValidated {
    }
}

