package com.mugreparty.steamdeck_remote_control;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "java.awt.headless=true"
})
class SteamdeckRemoteControlApplicationTests {

	@Test
	void contextLoads() {
		// This test will still fail in headless environment due to InputExecutor
		// but at least our new components should not be the cause
	}

}
