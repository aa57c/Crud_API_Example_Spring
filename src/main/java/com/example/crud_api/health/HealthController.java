package com.example.crud_api.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health Check", description = "API health and status endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "API Health Check",
            description = "Returns the current status and timestamp of the API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "API is healthy and running"
    )
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "service", "Student Management API"
        ));
    }

    @GetMapping("/info")
    @Operation(
            summary = "API Information",
            description = "Returns information about the API including available endpoints"
    )
    @ApiResponse(
            responseCode = "200",
            description = "API information retrieved successfully"
    )
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "name", "Student Management API",
                "description", "CRUD API for managing student records",
                "version", "1.0.0",
                "swagger-ui", "http://localhost:8080/swagger-ui.html",
                "api-docs", "http://localhost:8080/api-docs"
        ));
    }
}