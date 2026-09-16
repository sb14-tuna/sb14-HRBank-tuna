package com.sb14.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrbankApplication.class, args);
	}

}
