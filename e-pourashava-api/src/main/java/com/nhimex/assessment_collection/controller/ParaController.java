package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.ParaResponseDto;
import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.service.command.ParaCommand;
import com.nhimex.assessment_collection.service.mapper.ParaMapper;
import com.nhimex.assessment_collection.service.query.ParaQueryService;
import com.nhimex.assessment_collection.service.query.WordQueryService;
import com.nhimex.assessment_collection.service.validator.ParaValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/paras")
@RequiredArgsConstructor
@Tag(name = "Paras", description = "Para management endpoints")
public class ParaController {

    private final ParaQueryService queryService;
    private final ParaCommand command;
    private final ParaValidatorService validator;
    private final ParaMapper mapper;
    private final WordQueryService wordQueryService;

    @GetMapping
    @Operation(summary = "Get all paras")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getAll() {
        List<Para> paras = queryService.findAll();
        List<ParaResponseDto> response = paras.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get para by ID")
    public ResponseEntity<ApiResponse<ParaResponseDto>> getById(@PathVariable Long id) {
        Para para = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(para)));
    }

    @GetMapping("/by-word/{wordId}")
    @Operation(summary = "Get paras by word ID")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getByWordId(@PathVariable Long wordId) {
        List<Para> paras = queryService.findByWordId(wordId);
        List<ParaResponseDto> response = paras.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get paras by subdomain")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getBySubdomain(@PathVariable String subdomain) {
        List<Para> paras = queryService.findBySubdomain(subdomain);
        List<ParaResponseDto> response = paras.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-word/{wordId}/subdomain/{subdomain}")
    @Operation(summary = "Get paras by word ID and subdomain")
    public ResponseEntity<ApiResponse<List<ParaResponseDto>>> getByWordIdAndSubdomain(
            @PathVariable Long wordId, @PathVariable String subdomain) {
        List<Para> paras = queryService.findByWordIdAndSubdomain(wordId, subdomain);
        List<ParaResponseDto> response = paras.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create para")
    public ResponseEntity<ApiResponse<ParaResponseDto>> create(@RequestBody Para para) {
        if (para.getWord() != null && para.getWord().getId() != null) {
            Word word = wordQueryService.findById(para.getWord().getId());
            para.setWord(word);
        }
        validator.validateForCreate(para.getPbrName(),
                para.getWord() != null ? para.getWord().getId() : null,
                para.getSubdomain());
        Para saved = command.create(para);
        return ResponseEntity.ok(ApiResponse.success("Para created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update para")
    public ResponseEntity<ApiResponse<ParaResponseDto>> update(@PathVariable Long id, @RequestBody Para para) {
        if (para.getWord() != null && para.getWord().getId() != null) {
            Word word = wordQueryService.findById(para.getWord().getId());
            para.setWord(word);
        }
        validator.validateForUpdate(id, para.getPbrName(),
                para.getWord() != null ? para.getWord().getId() : null,
                para.getSubdomain());
        Para updated = command.update(id, para);
        return ResponseEntity.ok(ApiResponse.success("Para updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete para")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Para deleted successfully", null));
    }
}
