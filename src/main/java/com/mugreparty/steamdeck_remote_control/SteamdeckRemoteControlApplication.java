package com.mugreparty.steamdeck_remote_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SteamdeckRemoteControlApplication {

	public static void main(String[] args) {
    	System.setProperty("java.awt.headless", "false");
		SpringApplication.run(SteamdeckRemoteControlApplication.class, args);
	}

}
