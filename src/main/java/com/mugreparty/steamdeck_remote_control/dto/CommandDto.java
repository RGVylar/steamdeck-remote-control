package com.mugreparty.steamdeck_remote_control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mugreparty.steamdeck_remote_control.dto.payload.CommandPayload;
import com.mugreparty.steamdeck_remote_control.dto.payload.KeyboardPayload;
import com.mugreparty.steamdeck_remote_control.dto.payload.MousePayload;
import com.mugreparty.steamdeck_remote_control.dto.payload.TextPayload;
import com.mugreparty.steamdeck_remote_control.enums.CommandAction;
import com.mugreparty.steamdeck_remote_control.enums.CommandType;

/**
 * DTO principal para mandar comandos a traves de la API.
 * Ejemplo:
 * {
 *   "type": "KEYBOARD",
 *   "action": "PRESS",
 *   "payload": {"key": "CTRL+L"},
 *   "target": "local"
 * }
 */
@JsonPropertyOrder({ "type", "action", "payload", "target", "id", "ts" })
public record CommandDto (
    @NotNull String id,
    @NotNull CommandType type, // KEYBOARD, MOUSE, TEXT_INPUT, MACRO
    @NotNull CommandAction action, // PRESS RELEASE, MOVE, CLICK, TYPE...
    
    @com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type"
    )
    @com.fasterxml.jackson.annotation.JsonSubTypes({
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = TextPayload.class,     name = "TEXT_INPUT"),
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = KeyboardPayload.class, name = "KEYBOARD"),
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = MousePayload.class,    name = "MOUSE")
    })
    @NotNull CommandPayload payload,
    @NotBlank String target, // local
    long ts // timestamp
)
{
    public static CommandDto create(
        CommandType type,
        CommandAction action,
        CommandPayload payload
    ) {
        return new CommandDto(
            UUID.randomUUID().toString(),
            type,
            action,
            payload,
            "local",
            System.currentTimeMillis()
        );
    }
}