package com.mugreparty.steamdeck_remote_control.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;

@Component
public class KafkaCommandProducer {

    private final KafkaTemplate<String, CommandDto> kafkaTemplate;
    private final String topic;

    public KafkaCommandProducer(
            KafkaTemplate<String, CommandDto> kafkaTemplate,
            @Value("${app.kafka.topic.commands:commands}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(CommandDto dto) {
        // Usamos "target" como clave para mantener orden por dispositivo
        String key = (dto.target() == null || dto.target().isBlank()) ? "local" : dto.target();
        kafkaTemplate.send(topic, key, dto);
    }
    
}
