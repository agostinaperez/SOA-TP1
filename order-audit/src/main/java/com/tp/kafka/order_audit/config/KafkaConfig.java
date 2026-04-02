package com.tp.kafka.order_audit.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
public class KafkaConfig {

    /** * Intervalo de tiempo en milisegundos entre cada intento de reintento. 
     * Valor por defecto: 2000ms.
     */
    @Value("${app.kafka.retry.interval:2000}")
    private Long retryInterval;

    /** * Cantidad máxima de intentos de procesamiento antes de agotar los reintentos. 
     * Valor por defecto: 3 intentos.
     */
    @Value("${app.kafka.retry.max-attempts:3}")
    private Long maxAttempts;
    
    /**
     * Configura el manejador de errores global para los Listeners de Kafka.
     * <p>
     * El {@link DefaultErrorHandler} utiliza una política de {@link FixedBackOff} para reintentar
     * fallos temporales. En caso de agotar los reintentos o encontrar errores fatales, 
     * registra un log detallado en una sola línea para facilitar el monitoreo.
     * </p>
     * * <p><strong>Resiliencia:</strong> Se han añadido excepciones de deserialización como "No Reintentables"
     * para evitar que mensajes con formato JSON inválido (Poison Pills) bloqueen el avance del offset.</p>
     *
     * @return Una instancia configurada de {@link DefaultErrorHandler}.
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        FixedBackOff backOff = new FixedBackOff(retryInterval, maxAttempts);

        DefaultErrorHandler handler = new DefaultErrorHandler((record, exception) -> {
        log.error("ERROR KAFKA | Topic: {} | Part: {} | Offset: {} | Value: {} | Cause: {}", 
            record.topic(), 
            record.partition(), 
            record.offset(), 
            record.value(), 
            (exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage())
        );
        }, backOff);

        handler.addNotRetryableExceptions(org.springframework.kafka.support.serializer.DeserializationException.class);
        handler.addNotRetryableExceptions(com.fasterxml.jackson.core.JsonParseException.class);
        
        return handler;
    }
}