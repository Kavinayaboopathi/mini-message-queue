package com.kavinaya.minimessagequeue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniMessageQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniMessageQueueApplication.class, args);
	}

}
