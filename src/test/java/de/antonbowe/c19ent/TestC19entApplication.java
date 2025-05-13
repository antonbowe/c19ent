package de.antonbowe.c19ent;

import org.springframework.boot.SpringApplication;

public class TestC19entApplication {

	public static void main(String[] args) {
		SpringApplication.from(C19entApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
