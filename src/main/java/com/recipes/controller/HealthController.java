package com.recipes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {


    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Recipes API");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/info")
    public ResponseEntity<?> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Recipes API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("backend", "Spring Boot 3");
        return ResponseEntity.ok(response);
    }
}

