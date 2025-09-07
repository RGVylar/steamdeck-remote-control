package com.mugreparty.steamdeck_remote_control.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;
import com.mugreparty.steamdeck_remote_control.messaging.KafkaCommandProducer;

@Service
public class CommandService {
    private final KafkaCommandProducer producer;
    
    public CommandService(KafkaCommandProducer producer) {
        this.producer = producer;
    }

    /**
     * Valida lo basico y publica en kafka
     * Devuelve el id
     */
    public String handle(CommandDto dto) {
        // Validaciones basicas
        if (dto == null) throw new IllegalArgumentException("CommandDto no puede ser null");
        if (dto.action() == null) throw new IllegalArgumentException("action es obligatorio");

        String id = UUID.randomUUID().toString();
        long ts = Instant.now().toEpochMilli();
        
        // Pongo bonito el mensaje
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("id", id);
        envelope.put("ts", ts);
        envelope.put("action", dto.action());
        envelope.put("payload", dto.payload() == null ? Map.of() : dto.payload());
        envelope.put("target", "local");

        producer.send(envelope);

        return "";
    }

}   
