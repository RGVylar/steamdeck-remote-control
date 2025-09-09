package com.mugreparty.steamdeck_remote_control.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserAutoOpen {

  private boolean opened = false;

  @EventListener
  public void onWebServerReady(WebServerInitializedEvent event) {
    if (opened) return; // evita abrir dos veces
    opened = true;

    int port = event.getWebServer().getPort();
    String url = "http://localhost:" + port + "/qr"; // pagina que muestra

    try {
      if (Desktop.isDesktopSupported()) {
        new Thread(() -> {
          try { Thread.sleep(300); } catch (InterruptedException ignored) {}
          try { Desktop.getDesktop().browse(URI.create(url)); }
          catch (Exception e) { e.printStackTrace(); }
        }, "open-browser").start();
      } else {
        System.out.println("Abre manualmente: " + url);
      }
    } catch (Exception e) {
      System.out.println("No pude abrir navegador, URL: " + url);
      e.printStackTrace();
    }
  }
}
