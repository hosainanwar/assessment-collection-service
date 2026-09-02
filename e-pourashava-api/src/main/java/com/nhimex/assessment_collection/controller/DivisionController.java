package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.DivisionResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.service.DivisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/divisions")
@RequiredArgsConstructor
@Tag(name = "Divisions", description = "Division management endpoints")
public class DivisionController {

    private final DivisionService divisionService;

    @GetMapping
    @PreAuthorize("hasAuthority('DIVISION:READ')")
    @Operation(summary = "Get all divisions")
    public ResponseEntity<ApiResponse<List<DivisionResponseDto>>> getAll() {
        List<DivisionResponseDto> divisions = divisionService.findAll();
        return ResponseEntity.ok(ApiResponse.success(divisions));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DIVISION:READ')")
    @Operation(summary = "Get division by ID")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> getById(@PathVariable Long id) {
        DivisionResponseDto division = divisionService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(division));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DIVISION:CREATE')")
    @Operation(summary = "Create division")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> create(@RequestBody Division division) {
        DivisionResponseDto created = divisionService.create(division);
        return ResponseEntity.ok(ApiResponse.success("Division created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DIVISION:UPDATE')")
    @Operation(summary = "Update division")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> update(@PathVariable Long id, @RequestBody Division division) {
        DivisionResponseDto updated = divisionService.update(id, division);
        return ResponseEntity.ok(ApiResponse.success("Division updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DIVISION:DELETE')")
    @Operation(summary = "Delete division")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        divisionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Division deleted successfully", null));
    }
}
