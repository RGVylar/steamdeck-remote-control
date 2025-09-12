package com.mugreparty.steamdeck_remote_control.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.mugreparty.steamdeck_remote_control.dto.DeviceInfo;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PresencePinger {

  private final KafkaTemplate<String, DeviceInfo> kafka;
  @Value("${app.device-id}") String deviceId;

  @Scheduled(initialDelay = 2000, fixedRate = 5000)
  public void beat() throws UnknownHostException {
    var host = InetAddress.getLocalHost();
    var info = new DeviceInfo(
      deviceId,
      System.getenv().getOrDefault("COMPUTERNAME", deviceId),
      host.getHostName(),
      host.getHostAddress(),
      System.currentTimeMillis()
    );
    kafka.send("presence", deviceId, info);
  }
}
