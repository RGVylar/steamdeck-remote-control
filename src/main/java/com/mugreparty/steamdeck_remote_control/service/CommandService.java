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
        if (dto.type() == null) throw new IllegalArgumentException("type es obligatorio");

        String id = UUID.randomUUID().toString();
        long ts = Instant.now().toEpochMilli();
        String target = (dto.target() == null || dto.target().isBlank()) ? "local" : dto.target();
        
        // Pongo bonito el mensaje
         CommandDto normalized = new CommandDto(
            id,
            dto.type(),
            dto.action(),
            dto.payload(),
            target,
            ts
        );

        producer.send(normalized);

        return id;
    }

}   
