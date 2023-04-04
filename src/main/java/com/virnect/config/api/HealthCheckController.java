package com.virnect.config.api;

import java.time.ZonedDateTime;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author delbert park
 * @project PF-Config
 * @email delbert@virnect.com
 * @description
 * @since 2020.09.21
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthCheckController {

	private final BuildProperties buildProperties;

	@GetMapping("/healthcheck")
	public ResponseEntity<String> healthCheck() {
		String message =
			"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT CONFIG SERVER\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_VERSION: [ " + buildProperties.getVersion() + " ]\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";

		return ResponseEntity.ok(message);
	}
}
