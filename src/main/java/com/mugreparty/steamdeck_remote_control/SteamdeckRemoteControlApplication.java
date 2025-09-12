package com.mugreparty.steamdeck_remote_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class SteamdeckRemoteControlApplication {

    public static void main(String[] args) {
        // mostrar el dialogo y recuperar config
        Properties chosen = Launcher.askConfigIfNeeded();

        // aseguramos que se pueda abrir UI
        System.setProperty("java.awt.headless", "false");

        // arrancar Spring con esas propiedades por defecto
        SpringApplication app = new SpringApplication(SteamdeckRemoteControlApplication.class);
        app.setDefaultProperties((Map) chosen);
        app.run(args);
    }

}
