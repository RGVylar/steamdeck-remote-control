package com.mugreparty.steamdeck_remote_control.dto.payload;

import jakarta.validation.constraints.NotBlank;

public final class TextPayload implements CommandPayload {
  @NotBlank public String text;
  public Boolean perKey;   // teclas
  public Integer delayMs;

  public TextPayload() {}
  public TextPayload(String text, Boolean perKey, Integer delayMs) {
    this.text = text; this.perKey = perKey; this.delayMs = delayMs;
  }
}
