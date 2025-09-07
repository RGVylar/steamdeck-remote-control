package com.mugreparty.steamdeck_remote_control.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;
import com.mugreparty.steamdeck_remote_control.system.InputExecutor;

@Component
public class KafkaCommandConsumer {

    private final InputExecutor executor;

    public KafkaCommandConsumer(InputExecutor executor) {
        this.executor = executor;
    }

    @KafkaListener(topics = "commands", groupId = "remote-executor")
    public void consume(CommandDto command) {
        System.out.println("Received command: " + command);
        executor.execute(command);
    }    
}
