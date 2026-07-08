package com.example.Idea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class IdeaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeaApplication.class, args);
	}

}
