package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.DistrictResponseDto;
import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.service.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
@Tag(name = "Districts", description = "District management endpoints")
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    @Operation(summary = "Get all districts")
    public ResponseEntity<ApiResponse<List<DistrictResponseDto>>> getAll() {
        List<DistrictResponseDto> districts = districtService.findAll();
        return ResponseEntity.ok(ApiResponse.success(districts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get district by ID")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> getById(@PathVariable Long id) {
        DistrictResponseDto district = districtService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(district));
    }

    @GetMapping("/by-division/{divisionId}")
    @Operation(summary = "Get districts by division ID")
    public ResponseEntity<ApiResponse<List<DistrictResponseDto>>> getByDivisionId(@PathVariable Long divisionId) {
        List<DistrictResponseDto> districts = districtService.findByDivisionId(divisionId);
        return ResponseEntity.ok(ApiResponse.success(districts));
    }

    @PostMapping
    @Operation(summary = "Create district")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> create(@RequestBody District district) {
        DistrictResponseDto created = districtService.create(district);
        return ResponseEntity.ok(ApiResponse.success("District created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update district")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> update(@PathVariable Long id, @RequestBody District district) {
        DistrictResponseDto updated = districtService.update(id, district);
        return ResponseEntity.ok(ApiResponse.success("District updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete district")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        districtService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("District deleted successfully", null));
    }
}
