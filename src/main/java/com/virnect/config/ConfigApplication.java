package com.virnect.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		String msg = "\n\n----------------------------------------------------\n" + "\n"
			+ "   Platform Config Server is ready!\n"
			+ "   ---------------------------\n" + "\n"
			+ "   * CONFIG_ENV: [" + System.getenv("CONFIG_ENV") + "]\n" + "\n"
			+ "   * VIRNECT_ENV: [" + System.getenv("VIRNECT_ENV") + "]\n" + "\n"			
			+ "----------------------------------------------------\n";

		log.info(msg);
	}

}
