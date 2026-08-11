package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.ParaResponseDto;
import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.service.ParaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paras")
@RequiredArgsConstructor
@Tag(name = "Paras", description = "Para management endpoints")
public class ParaController {

    private final ParaService paraService;

    @GetMapping
    @Operation(summary = "Get all paras")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getAll() {
        List<ParaResponseDto> paras = paraService.findAll();
        return ResponseEntity.ok(ApiResponse.success(paras));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get para by ID")
    public ResponseEntity<ApiResponse<ParaResponseDto>> getById(@PathVariable Long id) {
        ParaResponseDto para = paraService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(para));
    }

    @GetMapping("/by-word/{wordId}")
    @Operation(summary = "Get paras by word ID")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getByWordId(@PathVariable Long wordId) {
        List<ParaResponseDto> paras = paraService.findByWordId(wordId);
        return ResponseEntity.ok(ApiResponse.success(paras));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get paras by subdomain")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getBySubdomain(@PathVariable String subdomain) {
        List<ParaResponseDto> paras = paraService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(paras));
    }

    @GetMapping("/by-word/{wordId}/subdomain/{subdomain}")
    @Operation(summary = "Get paras by word ID and subdomain")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getByWordIdAndSubdomain(
            @PathVariable Long wordId, @PathVariable String subdomain) {
        List<ParaResponseDto> paras = paraService.findByWordIdAndSubdomain(wordId, subdomain);
        return ResponseEntity.ok(ApiResponse.success(paras));
    }

    @PostMapping
    @Operation(summary = "Create para")
    public ResponseEntity<ApiResponse<ParaResponseDto>> create(@RequestBody Para para) {
        ParaResponseDto created = paraService.create(para);
        return ResponseEntity.ok(ApiResponse.success("Para created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update para")
    public ResponseEntity<ApiResponse<ParaResponseDto>> update(@PathVariable Long id, @RequestBody Para para) {
        ParaResponseDto updated = paraService.update(id, para);
        return ResponseEntity.ok(ApiResponse.success("Para updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete para")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        paraService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Para deleted successfully", null));
    }
}
