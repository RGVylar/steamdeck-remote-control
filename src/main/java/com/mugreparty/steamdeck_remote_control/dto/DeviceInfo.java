package com.mugreparty.steamdeck_remote_control.dto;

public record DeviceInfo(
  String id,        // app.device-id
  String name,      // opcional: Windows hostname bonito
  String host,
  String ip,
  long   lastSeenMs
) {}
