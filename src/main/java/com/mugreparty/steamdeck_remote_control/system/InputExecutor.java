package com.mugreparty.steamdeck_remote_control.system;

import com.mugreparty.steamdeck_remote_control.dto.CommandDto;
import com.mugreparty.steamdeck_remote_control.enums.CommandType;
import com.mugreparty.steamdeck_remote_control.dto.payload.MousePayload;
import com.mugreparty.steamdeck_remote_control.dto.payload.SystemPayload;
import com.mugreparty.steamdeck_remote_control.dto.payload.TextPayload;
import com.mugreparty.steamdeck_remote_control.enums.CommandAction;


import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@Component
public class InputExecutor {
  private final Robot robot;

  public InputExecutor() {
    try {
      this.robot = new Robot();
      this.robot.setAutoDelay(5);
    } catch (AWTException e) {
      throw new IllegalStateException("No se pudo inicializar Robot", e);
    }
  }

  public void execute(CommandDto dto) {
    try {
      if (dto.type() == CommandType.MOUSE) handleMouse(dto);
      else if (dto.type() == CommandType.TEXT_INPUT) handleText(dto);
      else if (dto.type() == CommandType.KEYBOARD) handleKeyboard(dto); // TODO: parser "CTRL+L"
      else if (dto.type() == CommandType.SYSTEM) handleSystem(dto);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleMouse(CommandDto dto) {
    var p = (MousePayload) dto.payload();
    if (dto.action() == CommandAction.MOVE) {
        // Soporta absoluto (x,y) o relativo (dx,dy)
        if (p.x != null && p.y != null) {
            robot.mouseMove(p.x, p.y);
        } else {
            var loc = java.awt.MouseInfo.getPointerInfo().getLocation();
            int nx = loc.x + (p.dx == null ? 0 : p.dx);
            int ny = loc.y + (p.dy == null ? 0 : p.dy);
            robot.mouseMove(nx, ny);
        }
    } else if (dto.action() == CommandAction.CLICK) {
      int mask = switch (p.button == null ? "LEFT" : p.button.toUpperCase()) {
        case "RIGHT" -> InputEvent.BUTTON3_DOWN_MASK;
        case "MIDDLE" -> InputEvent.BUTTON2_DOWN_MASK;
        default -> InputEvent.BUTTON1_DOWN_MASK;
      };
      robot.mousePress(mask);
      robot.mouseRelease(mask);
    } else if (dto.action() == CommandAction.SCROLL && p.wheel != null) {
      robot.mouseWheel(p.wheel);
    }
  }

  private void handleText(CommandDto dto) {
    var p = (TextPayload) dto.payload();
    String text = p.text == null ? "" : p.text;
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    robot.keyPress(KeyEvent.VK_CONTROL);
    robot.keyPress(KeyEvent.VK_V);
    robot.keyRelease(KeyEvent.VK_V);
    robot.keyRelease(KeyEvent.VK_CONTROL);
    robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
  }

  private void handleKeyboard(CommandDto dto) {
    // TODO: implementar parser "CTRL+L", "ALT+TAB", etc. (mapeo a KeyEvent)
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  private void handleSystem(CommandDto dto) {
    if (isWindows()) {
      switch (dto.action()) {
        // Volumen
        case VOLUME_UP    -> WindowsKeys.volumeUp();
        case VOLUME_DOWN  -> WindowsKeys.volumeDown();
        case TOGGLE_MUTE  -> WindowsKeys.toggleMute();

        // Media
        case PLAY_PAUSE   -> WindowsKeys.playPause();
        case NEXT         -> WindowsKeys.nextTrack();
        case PREV         -> WindowsKeys.prevTrack();

        default -> throw new IllegalArgumentException("Acción SYSTEM no soportada: " + dto.action());
      }
      return;
    }
    handleSystemLinux(dto);
  }

  private void handleSystemLinux(CommandDto dto) {
    String[] cmd;
    switch (dto.action()) {
      case VOLUME_UP ->
        cmd = new String[]{"bash","-lc","pactl set-sink-volume @DEFAULT_SINK@ +5%"};
      case VOLUME_DOWN ->
        cmd = new String[]{"bash","-lc","pactl set-sink-volume @DEFAULT_SINK@ -5%"};
      case TOGGLE_MUTE ->
        cmd = new String[]{"bash","-lc","pactl set-sink-mute @DEFAULT_SINK@ toggle"};
      default -> throw new IllegalArgumentException("Acción SYSTEM no soportada: " + dto.action());
    }
    try {
      new ProcessBuilder(cmd).inheritIO().start();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo ejecutar volumen vía pactl", e);
    }
  }

}
