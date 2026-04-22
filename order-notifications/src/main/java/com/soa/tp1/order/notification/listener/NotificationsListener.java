package com.soa.tp1.order.notification.listener;

import com.soa.tp1.order.notification.dto.OrderValidated;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class NotificationsListener {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationsListener.class);
/*acá el método recibe el evento de orden validada, el consumerRecord que es el mensaje de kafka completo
(incluye metadata como el key, el topic, partition, offset), y el acknowledgement, que permite el control manual del offset
O sea kafka guarda con el comando ack.acknowledge() los mensajes que ya procesé*/
    @KafkaListener(
        topics = "orders.validated.v1",
        groupId = "notification-group",           
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderValidated(OrderValidated event, ConsumerRecord<String, OrderValidated> record, Acknowledgment ack) {
        
        log.info("[NOTIFICACIÓN] Procesando orden validada:");
        log.info("   Order ID: {}", event.orderId());
        log.info("   Cantidad: {}", event.amount());
        log.info("   Tipo de moneda: {}", event.currency());
        log.info("   Offset: {}, Partition: {}", record.offset(), record.partition());
        
        simulateNotificationSend(event.orderId());
        
        ack.acknowledge();  // Commit manual offset
        log.info("Offset {} committed for order {}", record.offset(), event.orderId());
    }
    
    private void simulateNotificationSend(String orderId) {
        log.info("Simulando envío email/SMS para orden {}", orderId);
    }
}
