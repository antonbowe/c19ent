package de.antonbowe.c19ent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class C19entApplicationTests {

	@Test
	void contextLoads() {
	}

}
