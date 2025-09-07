package com.mugreparty.steamdeck_remote_control.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;
import com.mugreparty.steamdeck_remote_control.service.CommandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/commands")
public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> postCommand(@Valid @RequestBody CommandDto dto) {
        // delega en el servicio; devuelve 202 + id
        String id = commandService.handle(dto);
        return ResponseEntity.accepted().body(Map.of("id", id));
    }

    /*@PostMapping
    public ResponseEntity<String> receiveRaw(@RequestBody String raw) {
        System.out.println(">>> RAW BODY: " + raw);
        return ResponseEntity.ok("recibido");
    }*/

}
