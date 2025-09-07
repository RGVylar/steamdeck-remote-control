package com.mugreparty.steamdeck_remote_control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO principal para mandar comandos a traves de la API.
 * Ejemplo:
 * {
 *   "type": "KEYBOARD",
 *   "action": "PRESS",
 *   "payload": {"key": "CTRL+L"}
 * }
 */
public record CommandDto (
    @NotNull String id,
    @NotNull CommandType type, // KEYBOARD, MOUSE, TEXT_INPUT, MACRO
    @NotNull CommandAction action, // PRESS RELEASE, MOVE, CLICK, TYPE...
    @NotNull Object payload,
    @NotBlank String target, // local
    long ts // timestamp
)
{
    public static CommandDto create(
        CommandType type,
        CommandAction action,
        Object payload
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