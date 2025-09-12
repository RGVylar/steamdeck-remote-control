package com.mugreparty.steamdeck_remote_control.messaging;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mugreparty.steamdeck_remote_control.dto.DeviceInfo;

@Component
public class PresenceCache {

  private final Map<String, DeviceInfo> devices = new ConcurrentHashMap<>();

  @KafkaListener(
    topics = "presence",
    groupId = "presence-cache-${app.device-id}",
    properties = {
        "spring.json.value.default.type=com.mugreparty.steamdeck_remote_control.dto.DeviceInfo",
        "spring.json.trusted.packages=com.mugreparty.steamdeck_remote_control.*,*"
    }
  )
  public void onPresence(DeviceInfo info) {
    devices.put(info.id(), info);
    }
  // Limpieza de obsoletos (cada 10 s)
  @Scheduled(fixedRate = 10000)
  public void cleanup() {
    long now = System.currentTimeMillis();
    devices.values().removeIf(d -> now - d.lastSeenMs() > 15000);
  }

  public Collection<DeviceInfo> list() {
    return devices.values().stream()
      .sorted(Comparator.comparing(DeviceInfo::name))
      .toList();
  }
}
