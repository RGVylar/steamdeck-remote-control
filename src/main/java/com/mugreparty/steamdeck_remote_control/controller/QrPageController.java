package com.mugreparty.steamdeck_remote_control.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.*;

@Controller
public class QrPageController {

  // Página que abrimos al iniciar (sirve el html estático)
  @GetMapping("/qr")
  public String qr() {
    return "forward:/qr.html";
  }

  // Info del servidor para que el front pueda mostrar IP/puerto
  @GetMapping("/api/v1/server-info")
  @ResponseBody
  public Map<String, String> serverInfo(HttpServletRequest req) {
    String ip = bestLocalIPv4().orElse("localhost");
    int port = req.getLocalPort();
    return Map.of(
      "ip", ip,
      "port", String.valueOf(port),
      "url", "http://" + ip + ":" + port + "/"
    );
  }

  // Intenta elegir una IPv4 de la LAN (evita loopback/virtuales)
  private Optional<String> bestLocalIPv4() {
    try {
      List<String> candidates = new ArrayList<>();
      Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      while (ifaces.hasMoreElements()) {
        NetworkInterface ni = ifaces.nextElement();
        if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
          InetAddress a = addrs.nextElement();
          if (a instanceof Inet4Address && !a.isLoopbackAddress()) {
            String host = a.getHostAddress();
            candidates.add(host);
            if (a.isSiteLocalAddress()) return Optional.of(host); // 192.168/10.x/172.16-31
          }
        }
      }
      if (!candidates.isEmpty()) return Optional.of(candidates.get(0));
    } catch (Exception ignored) {}
    return Optional.empty();
  }
}
