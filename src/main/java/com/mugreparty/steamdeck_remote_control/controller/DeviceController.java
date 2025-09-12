package com.mugreparty.steamdeck_remote_control.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mugreparty.steamdeck_remote_control.dto.DeviceInfo;
import com.mugreparty.steamdeck_remote_control.messaging.PresenceCache;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DeviceController {

  private final PresenceCache cache;

  @GetMapping("/devices")
  public Collection<DeviceInfo> devices() {
    return cache.list();
  }
}
