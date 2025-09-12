package com.mugreparty.steamdeck_remote_control;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public final class Launcher {
  private static final Path CFG_DIR  = Paths.get(System.getProperty("user.home"), ".steamdeck-remote-control");
  private static final Path CFG_FILE = CFG_DIR.resolve("config.properties");

  public static Properties askConfigIfNeeded() {
    System.setProperty("java.awt.headless", "false"); // asegúrate de poder abrir UI

    Properties p = loadProps();
    String defDevice = p.getProperty("app.device-id", guessHostname());
    String defKafka  = p.getProperty("spring.kafka.bootstrap-servers", "192.168.1.132:9092");
    String defPort   = p.getProperty("server.port", "8080");

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(4,4,4,4); c.fill = GridBagConstraints.HORIZONTAL;

    JTextField deviceField = new JTextField(defDevice, 18);
    JTextField kafkaField  = new JTextField(defKafka, 18);
    JTextField portField   = new JTextField(defPort, 6);

    int y=0;
    c.gridx=0; c.gridy=y; panel.add(new JLabel("Device ID:"), c);
    c.gridx=1; panel.add(deviceField, c); y++;
    c.gridx=0; c.gridy=y; panel.add(new JLabel("Kafka bootstrap:"), c);
    c.gridx=1; panel.add(kafkaField, c); y++;
    c.gridx=0; c.gridy=y; panel.add(new JLabel("Server port:"), c);
    c.gridx=1; panel.add(portField, c); y++;

    int res = JOptionPane.showConfirmDialog(null, panel, "Steamdeck Remote Control",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (res != JOptionPane.OK_OPTION) System.exit(0);

    String device = deviceField.getText().trim();
    String kafka  = kafkaField.getText().trim();
    String port   = portField.getText().trim();

    if (device.isEmpty() || kafka.isEmpty() || port.isEmpty()) {
      JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
      System.exit(1);
    }

    p.setProperty("app.device-id", device);
    p.setProperty("spring.kafka.bootstrap-servers", kafka);
    p.setProperty("server.port", port);
    saveProps(p);
    return p;
  }

  private static Properties loadProps() {
    Properties p = new Properties();
    try {
      if (Files.exists(CFG_FILE)) try (InputStream in = Files.newInputStream(CFG_FILE)) { p.load(in); }
    } catch (IOException ignored) {}
    return p;
  }

  private static void saveProps(Properties p) {
    try {
      if (!Files.exists(CFG_DIR)) Files.createDirectories(CFG_DIR);
      try (OutputStream out = Files.newOutputStream(CFG_FILE)) { p.store(out, "Steamdeck Remote Control"); }
    } catch (IOException ignored) {}
  }

  private static String guessHostname() {
    try { return java.net.InetAddress.getLocalHost().getHostName(); }
    catch (Exception e) { return "device-"+System.currentTimeMillis(); }
  }
}
