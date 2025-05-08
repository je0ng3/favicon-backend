package com.capstone.favicon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaviconApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaviconApplication.class, args);
	}

}
