package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.PourashavaResponseDto;
import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.service.command.PourashavaCommand;
import com.nhimex.assessment_collection.service.mapper.PourashavaMapper;
import com.nhimex.assessment_collection.service.query.DistrictQueryService;
import com.nhimex.assessment_collection.service.query.DivisionQueryService;
import com.nhimex.assessment_collection.service.query.PourashavaQueryService;
import com.nhimex.assessment_collection.service.validator.PourashavaValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pourashavas")
@RequiredArgsConstructor
@Tag(name = "Pourashavas", description = "Pourashava management endpoints")
public class PourashavaController {

    private final PourashavaQueryService queryService;
    private final PourashavaCommand command;
    private final PourashavaValidatorService validator;
    private final PourashavaMapper mapper;
    private final DivisionQueryService divisionQueryService;
    private final DistrictQueryService districtQueryService;

    @GetMapping
    @Operation(summary = "Get all pourashavas")
    public ResponseEntity<ApiResponse<List<PourashavaResponseDto>>> getAll() {
        List<Pourashava> pourashavas = queryService.findAll();
        List<PourashavaResponseDto> response = pourashavas.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pourashava by ID")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> getById(@PathVariable Long id) {
        Pourashava pourashava = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(pourashava)));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get pourashava by subdomain")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> getBySubdomain(@PathVariable String subdomain) {
        Pourashava pourashava = queryService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(pourashava)));
    }

    @GetMapping("/by-district/{districtId}")
    @Operation(summary = "Get pourashavas by district ID")
    public ResponseEntity<ApiResponse<List<PourashavaResponseDto>>> getByDistrictId(@PathVariable Long districtId) {
        List<Pourashava> pourashavas = queryService.findByDistrictId(districtId);
        List<PourashavaResponseDto> response = pourashavas.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-division/{divisionId}")
    @Operation(summary = "Get pourashavas by division ID")
    public ResponseEntity<ApiResponse<List<PourashavaResponseDto>>> getByDivisionId(@PathVariable Long divisionId) {
        List<Pourashava> pourashavas = queryService.findByDivisionId(divisionId);
        List<PourashavaResponseDto> response = pourashavas.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create pourashava")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> create(@RequestBody Pourashava pourashava) {
        Division division = divisionQueryService.findById(pourashava.getDivision().getId());
        District district = districtQueryService.findById(pourashava.getDistrict().getId());
        pourashava.setDivision(division);
        pourashava.setDistrict(district);

        validator.validateForCreate(pourashava.getBnName(), pourashava.getEnName(),
                pourashava.getSubdomain(), pourashava.getDivision().getId(), pourashava.getDistrict().getId());
        Pourashava saved = command.create(pourashava);
        return ResponseEntity.ok(ApiResponse.success("Pourashava created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pourashava")
    public ResponseEntity<ApiResponse<PourashavaResponseDto>> update(@PathVariable Long id, @RequestBody Pourashava pourashava) {
        if (pourashava.getDivision() != null && pourashava.getDivision().getId() != null) {
            Division division = divisionQueryService.findById(pourashava.getDivision().getId());
            pourashava.setDivision(division);
        }
        if (pourashava.getDistrict() != null && pourashava.getDistrict().getId() != null) {
            District district = districtQueryService.findById(pourashava.getDistrict().getId());
            pourashava.setDistrict(district);
        }

        validator.validateForUpdate(id, pourashava.getBnName(), pourashava.getEnName(),
                pourashava.getSubdomain(),
                pourashava.getDivision() != null ? pourashava.getDivision().getId() : null,
                pourashava.getDistrict() != null ? pourashava.getDistrict().getId() : null);
        Pourashava updated = command.update(id, pourashava);
        return ResponseEntity.ok(ApiResponse.success("Pourashava updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pourashava")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Pourashava deleted successfully", null));
    }
}
