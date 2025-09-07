package com.mugreparty.steamdeck_remote_control.messaging;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommandProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaCommandProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topic.commands:commands}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(Map<String, Object> envelope) {
        // Usamos "target" como clave para mantener orden por dispositivo
        String key = String.valueOf(envelope.getOrDefault("target", "local"));
        kafkaTemplate.send(topic, key, envelope);
    }
    
}
