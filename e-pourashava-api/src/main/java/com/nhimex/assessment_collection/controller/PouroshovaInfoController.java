package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.PouroshovaInfoResponseDto;
import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.service.command.PouroshovaInfoCommand;
import com.nhimex.assessment_collection.service.mapper.PouroshovaInfoMapper;
import com.nhimex.assessment_collection.service.query.PouroshovaInfoQueryService;
import com.nhimex.assessment_collection.service.validator.PouroshovaInfoValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pouroshova-infos")
@RequiredArgsConstructor
@Tag(name = "Pouroshova Infos", description = "Pouroshova info management endpoints")
public class PouroshovaInfoController {

    private final PouroshovaInfoQueryService queryService;
    private final PouroshovaInfoCommand command;
    private final PouroshovaInfoValidatorService validator;
    private final PouroshovaInfoMapper mapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get pouroshova info by ID")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> getById(@PathVariable Long id) {
        PouroshovaInfo info = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(info)));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get pouroshova info by subdomain")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> getBySubdomain(@PathVariable String subdomain) {
        PouroshovaInfo info = queryService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(info)));
    }

    @PostMapping
    @Operation(summary = "Create pouroshova info")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> create(@RequestBody PouroshovaInfo pouroshovaInfo) {
        validator.validateForCreate(pouroshovaInfo.getPouroshovaName(), pouroshovaInfo.getMeyorName(),
                pouroshovaInfo.getPsName(), pouroshovaInfo.getDsName(),
                pouroshovaInfo.getSignatureName(), pouroshovaInfo.getSubdomain());
        PouroshovaInfo saved = command.create(pouroshovaInfo);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pouroshova info")
    public ResponseEntity<ApiResponse<PouroshovaInfoResponseDto>> update(@PathVariable Long id,
                                                                          @RequestBody PouroshovaInfo pouroshovaInfo) {
        validator.validateForUpdate(id, pouroshovaInfo.getPouroshovaName(), pouroshovaInfo.getMeyorName(),
                pouroshovaInfo.getPsName(), pouroshovaInfo.getDsName(),
                pouroshovaInfo.getSignatureName(), pouroshovaInfo.getSubdomain());
        PouroshovaInfo updated = command.update(id, pouroshovaInfo);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pouroshova info")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Pouroshova info deleted successfully", null));
    }
}
