package com.tp.kafka.order_audit.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.tp.kafka.order_audit.model.OrderValidated;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OrderAuditConsumer {



    /**
     * Punto de entrada para los mensajes de Kafka. Consume eventos de órdenes validadas.
     * <p>
     * El método extrae el cuerpo del mensaje y diversos encabezados de Kafka (headers)
     * para trazabilidad y debugging. Si el procesamiento falla, lanza una excepción
     * para que intervenga el ErrorHandler configurado.
     * </p>
     *
     * @param event     El objeto {@link OrderValidated} deserializado del cuerpo del mensaje.
     * @param key       La clave del mensaje (generalmente el Order ID como String).
     * @param offset    La posición del mensaje dentro de la partición de Kafka.
     * @param partition El número de partición desde donde se leyó el mensaje.
     * @param ts        El timestamp (milisegundos) de cuándo se produjo el mensaje.
     * @throws Exception Si ocurre un error durante el procesamiento de la auditoría.
     */
    @KafkaListener(
        topics = "${app.kafka.consumer.topic}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            OrderValidated event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts
    ) {
        log.info("[KAFKA METADATA] Key: {} | Partition: {} | Offset: {} | TS: {}", 
                 key, partition, offset, ts);
                 
    try {
            processAudit(event);
            log.debug("Audit processed successfully for key: {}", key);
        } catch (Exception e) {
            log.error("Error processing audit for key {}: {}", key, e.getMessage());
            throw e; 
        }
    }

    /**
     * Realiza la lógica de negocio de auditoría.
     * <p>
     * Actualmente formatea y emite un log informativo con los datos principales 
     * de la orden recibida.
     * </p>
     *
     * @param event El evento de orden validada a auditar.
     */
    private void processAudit(OrderValidated event) {

        log.info("AUDIT EVENT | Order ID: {} | Amount: {} | Currency: {} | Status: PROCESSED", 
        event.getOrderId(), event.getAmount(), event.getCurrency());
    }
}