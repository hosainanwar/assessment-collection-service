package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.PouroshovaInfoResponseDto;
import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.service.PouroshovaInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pouroshova-infos")
@RequiredArgsConstructor
@Tag(name = "Pouroshova Infos", description = "Pouroshova info management endpoints")
public class PouroshovaInfoController {

    private final PouroshovaInfoService pouroshovaInfoService;

    @GetMapping
    @Operation(summary = "Get all pouroshova infos")
    public ResponseEntity<ApiResponse<List<PouroshovaInfoResponseDto>>> getAll() {
        List<PouroshovaInfoResponseDto> infos = pouroshovaInfoService.findAll();
        return ResponseEntity.ok(ApiResponse.success(infos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pouroshova info by ID")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> getById(@PathVariable Long id) {
        PouroshovaInfoResponseDto info = pouroshovaInfoService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get pouroshova info by subdomain")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> getBySubdomain(@PathVariable String subdomain) {
        PouroshovaInfoResponseDto info = pouroshovaInfoService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @PostMapping
    @Operation(summary = "Create pouroshova info")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> create(@RequestBody PouroshovaInfo pouroshovaInfo) {
        PouroshovaInfoResponseDto created = pouroshovaInfoService.create(pouroshovaInfo);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pouroshova info")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> update(@PathVariable Long id,
                                                                          @RequestBody PouroshovaInfo pouroshovaInfo) {
        PouroshovaInfoResponseDto updated = pouroshovaInfoService.update(id, pouroshovaInfo);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pouroshova info")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pouroshovaInfoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info deleted successfully", null));
    }
}
