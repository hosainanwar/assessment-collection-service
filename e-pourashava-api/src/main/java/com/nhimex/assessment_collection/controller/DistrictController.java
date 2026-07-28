package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.DistrictResponseDto;
import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.service.command.DistrictCommand;
import com.nhimex.assessment_collection.service.mapper.DistrictMapper;
import com.nhimex.assessment_collection.service.query.DistrictQueryService;
import com.nhimex.assessment_collection.service.query.DivisionQueryService;
import com.nhimex.assessment_collection.service.validator.DistrictValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
@Tag(name = "Districts", description = "District management endpoints")
public class DistrictController {

    private final DistrictQueryService queryService;
    private final DistrictCommand command;
    private final DistrictValidatorService validator;
    private final DistrictMapper mapper;
    private final DivisionQueryService divisionQueryService;

    @GetMapping
    @Operation(summary = "Get all districts")
    public ResponseEntity<ApiResponse<List<DistrictResponseDto>>> getAll() {
        List<District> districts = queryService.findAll();
        List<DistrictResponseDto> response = districts.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get district by ID")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> getById(@PathVariable Long id) {
        District district = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(district)));
    }

    @GetMapping("/by-division/{divisionId}")
    @Operation(summary = "Get districts by division ID")
    public ResponseEntity<ApiResponse<List<DistrictResponseDto>>> getByDivisionId(@PathVariable Long divisionId) {
        List<District> districts = queryService.findByDivisionId(divisionId);
        List<DistrictResponseDto> response = districts.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search districts")
    public ResponseEntity<ApiResponse<Page<DistrictResponseDto>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long divisionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<District> districts = queryService.search(name, divisionId, page, size);
        Page<DistrictResponseDto> response = districts.map(mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create district")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> create(@RequestBody District district) {
        Division division = divisionQueryService.findById(district.getDivision().getId());
        district.setDivision(division);

        validator.validateForCreate(district.getName(), district.getEnName(), district.getDivision().getId());
        District saved = command.create(district);
        return ResponseEntity.ok(ApiResponse.success("District created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update district")
    public ResponseEntity<ApiResponse<DistrictResponseDto>> update(@PathVariable Long id, @RequestBody District district) {
        if (district.getDivision() != null && district.getDivision().getId() != null) {
            Division division = divisionQueryService.findById(district.getDivision().getId());
            district.setDivision(division);
        }

        validator.validateForUpdate(id, district.getName(), district.getEnName(),
                district.getDivision() != null ? district.getDivision().getId() : null);
        District updated = command.update(id, district);
        return ResponseEntity.ok(ApiResponse.success("District updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete district")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("District deleted successfully", null));
    }
}
