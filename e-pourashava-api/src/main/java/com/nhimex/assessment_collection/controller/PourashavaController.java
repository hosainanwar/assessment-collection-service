package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.PourashavaResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.service.PourashavaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pourashavas")
@RequiredArgsConstructor
@Tag(name = "Pourashavas", description = "Pourashava management endpoints")
public class PourashavaController {

    private final PourashavaService pourashavaService;

    @GetMapping
    @Operation(summary = "Get all pourashavas")
    public ResponseEntity<ApiResponse<List<PourashavaResponseDto>>> getAll() {
        List<PourashavaResponseDto> pourashavas = pourashavaService.findAll();
        return ResponseEntity.ok(ApiResponse.success(pourashavas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pourashava by ID")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> getById(@PathVariable Long id) {
        PourashavaResponseDto pourashava = pourashavaService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(pourashava));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get pourashava by subdomain")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> getBySubdomain(@PathVariable String subdomain) {
        PourashavaResponseDto pourashava = pourashavaService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(pourashava));
    }

    @PostMapping
    @Operation(summary = "Create pourashava")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> create(@RequestBody Pourashava pourashava) {
        PourashavaResponseDto created = pourashavaService.create(pourashava);
        return ResponseEntity.ok(ApiResponse.success("Pourashava created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pourashava")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> update(@PathVariable Long id, @RequestBody Pourashava pourashava) {
        PourashavaResponseDto updated = pourashavaService.update(id, pourashava);
        return ResponseEntity.ok(ApiResponse.success("Pourashava updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pourashava")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pourashavaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Pourashava deleted successfully", null));
    }
}
