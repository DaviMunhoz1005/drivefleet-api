package com.drivefleet.drivefleet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class DrivefleetApplicationTests {

	@Test
	void contextLoads() {
	}

}
