package com.mugreparty.steamdeck_remote_control.dto.payload;

import jakarta.validation.constraints.NotNull;

public final class MousePayload implements CommandPayload {
  @NotNull public Integer x;          // absolutos
  @NotNull public Integer y;
  @NotNull public Integer dx;          // relativos
  @NotNull public Integer dy;
  public String button;               // LEFT|RIGHT|MIDDLE (solo para CLICK)
  public Integer wheel;               // scroll (+/-) para SCROLL

  public MousePayload() {}
  public MousePayload(Integer x, Integer y, String button, Integer wheel) {
    this.x = x; this.y = y; this.button = button; this.wheel = wheel;
  }
}
