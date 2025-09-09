package com.mugreparty.steamdeck_remote_control.controller;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.awt.*;
import java.util.Map;

@RestController
public class SystemController {

  private final ConfigurableApplicationContext ctx;

  public SystemController(ConfigurableApplicationContext ctx) {
    this.ctx = ctx;
  }

  // Ruta absoluta
  @GetMapping("/api/v1/screen")
  public Map<String, Integer> getScreen() {
    System.setProperty("java.awt.headless", "false"); // por si acaso
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    DisplayMode dm = ge.getDefaultScreenDevice().getDisplayMode();
    return Map.of("width", dm.getWidth(), "height", dm.getHeight());
  }

  @PostMapping("/api/v1/system/exit")
  public ResponseEntity<Map<String, String>> exit() {
    new Thread(() -> {
      try { Thread.sleep(200); } catch (InterruptedException ignored) {}
      ctx.close();
      System.exit(0);
    }, "app-exit").start();

    return ResponseEntity.ok(Map.of("status", "shutting-down"));
  }
}
