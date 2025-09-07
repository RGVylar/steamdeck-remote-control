package com.mugreparty.steamdeck_remote_control.dto.payload;

import jakarta.validation.constraints.NotBlank;

public final class KeyboardPayload implements CommandPayload {
  @NotBlank public String key;    // p.ej. "CTRL+L", "ENTER", "A"
  public Integer repeat;          // opcional

  public KeyboardPayload() {}
  public KeyboardPayload(String key, Integer repeat) {
    this.key = key; this.repeat = repeat;
  }
}
