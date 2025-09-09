package com.mugreparty.steamdeck_remote_control.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;
import com.mugreparty.steamdeck_remote_control.system.InputExecutor;

@Component
public class KafkaCommandConsumer {

    private final InputExecutor executor;
    private final String deviceId;

        public KafkaCommandConsumer(
            InputExecutor executor,
            @Value("${app.device-id:local}") String deviceId // <- lee tu propiedad
    ) {
        this.executor = executor;
        this.deviceId = deviceId;
    }

    @KafkaListener(
    topics = "${app.kafka.topic.commands:commands}",
    groupId = "remote-executor-${app.device-id:local}"
    )
    public void consume(CommandDto dto) {
        // Filtra por target
        String tgt = dto.target();
        if (tgt == null || tgt.isBlank()) {
            return; // sin target, ignora
        }
        if (!tgt.equals(deviceId)) {
            return; // no es para este agente
        }
        System.out.println("[" + deviceId + "] Received command: " + dto);
        executor.execute(dto);
    }    
}
