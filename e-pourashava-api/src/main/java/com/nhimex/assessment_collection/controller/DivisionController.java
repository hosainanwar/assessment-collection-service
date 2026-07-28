package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.DivisionResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.service.command.DivisionCommand;
import com.nhimex.assessment_collection.service.mapper.DivisionMapper;
import com.nhimex.assessment_collection.service.query.DivisionQueryService;
import com.nhimex.assessment_collection.service.validator.DivisionValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/divisions")
@RequiredArgsConstructor
@Tag(name = "Divisions", description = "Division management endpoints")
public class DivisionController {

    private final DivisionQueryService queryService;
    private final DivisionCommand command;
    private final DivisionValidatorService validator;
    private final DivisionMapper mapper;

    @GetMapping
    @Operation(summary = "Get all divisions")
    public ResponseEntity<ApiResponse<List<DivisionResponseDto>>> getAll() {
        List<Division> divisions = queryService.findAll();
        List<DivisionResponseDto> response = divisions.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get division by ID")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> getById(@PathVariable Long id) {
        Division division = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(division)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search divisions")
    public ResponseEntity<ApiResponse<Page<DivisionResponseDto>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Division> divisions = queryService.search(name, page, size);
        Page<DivisionResponseDto> response = divisions.map(mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create division")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> create(@RequestBody Division division) {
        validator.validateForCreate(division.getName());
        Division saved = command.create(division);
        return ResponseEntity.ok(ApiResponse.success("Division created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update division")
    public ResponseEntity<ApiResponse<DivisionResponseDto>> update(@PathVariable Long id, @RequestBody Division division) {
        validator.validateForUpdate(id, division.getName());
        Division updated = command.update(id, division);
        return ResponseEntity.ok(ApiResponse.success("Division updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete division")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Division deleted successfully", null));
    }
}
