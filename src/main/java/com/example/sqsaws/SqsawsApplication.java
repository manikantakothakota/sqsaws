package com.example.sqsaws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SqsawsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqsawsApplication.class, args);
	}

}
