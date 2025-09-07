package com.mugreparty.steamdeck_remote_control.controller;

import org.springframework.web.bind.annotation.*;
import java.awt.*;
import java.util.Map;

@RestController
public class SystemController {

  // Ruta absoluta
  @GetMapping("/api/v1/screen")
  public Map<String, Integer> getScreen() {
    System.setProperty("java.awt.headless", "false"); // por si acaso
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    DisplayMode dm = ge.getDefaultScreenDevice().getDisplayMode();
    return Map.of("width", dm.getWidth(), "height", dm.getHeight());
  }
}
