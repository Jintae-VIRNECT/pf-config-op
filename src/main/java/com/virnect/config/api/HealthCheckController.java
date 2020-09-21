package com.virnect.config.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author delbert park
 * @project PF-Config
 * @email delbert@virnect.com
 * @description
 * @since 2020.09.21
 */
@RestController
@RequestMapping("/")
public class HealthCheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthCheck() {

        return ResponseEntity.ok("WELCOME CONFIG SERVER - " + LocalDateTime.now());
    }
}
