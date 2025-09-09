package com.mugreparty.steamdeck_remote_control.system;

import com.sun.jna.Library;
import com.sun.jna.Native;

final class WindowsKeys {
  private WindowsKeys() {}

  private interface User32 extends Library {
    User32 INSTANCE = Native.load("user32", User32.class);
    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
  }

  // Virtual-Key codes Windows
  // Volumen
  private static final byte VK_VOLUME_MUTE  = (byte) 0xAD;
  private static final byte VK_VOLUME_DOWN  = (byte) 0xAE;
  private static final byte VK_VOLUME_UP    = (byte) 0xAF;
  
  // Media
  private static final byte VK_MEDIA_NEXT_TRACK = (byte) 0xB0;
  private static final byte VK_MEDIA_PREV_TRACK = (byte) 0xB1;
  private static final byte VK_MEDIA_STOP       = (byte) 0xB2;
  private static final byte VK_MEDIA_PLAY_PAUSE = (byte) 0xB3;

  private static final int KEYEVENTF_EXTENDEDKEY = 0x0001;
  private static final int KEYEVENTF_KEYUP       = 0x0002;

  private static void tap(byte vk) {
    User32.INSTANCE.keybd_event(vk, (byte)0, KEYEVENTF_EXTENDEDKEY, 0);
    User32.INSTANCE.keybd_event(vk, (byte)0, KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
  }

  // Volumen
  static void volumeUp()   { tap(VK_VOLUME_UP); }
  static void volumeDown() { tap(VK_VOLUME_DOWN); }
  static void toggleMute() { tap(VK_VOLUME_MUTE); }
  
  // Media
  static void playPause()  { tap(VK_MEDIA_PLAY_PAUSE); }
  static void nextTrack()  { tap(VK_MEDIA_NEXT_TRACK); }
  static void prevTrack()  { tap(VK_MEDIA_PREV_TRACK); }
  static void stopTrack()  { tap(VK_MEDIA_STOP); }
}
