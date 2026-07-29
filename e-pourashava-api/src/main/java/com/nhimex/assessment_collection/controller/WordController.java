package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.service.command.WordCommand;
import com.nhimex.assessment_collection.service.mapper.WordMapper;
import com.nhimex.assessment_collection.service.query.WordQueryService;
import com.nhimex.assessment_collection.service.validator.WordValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
@Tag(name = "Words", description = "Word management endpoints")
public class WordController {

    private final WordQueryService queryService;
    private final WordCommand command;
    private final WordValidatorService validator;
    private final WordMapper mapper;

    @GetMapping
    @Operation(summary = "Get all words")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> getAll() {
        List<Word> words = queryService.findAll();
        List<WordResponseDto> response = words.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get word by ID")
    public ResponseEntity<ApiResponse<WordResponseDto>> getById(@PathVariable Long id) {
        Word word = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(word)));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @Operation(summary = "Get words by subdomain")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> getBySubdomain(@PathVariable String subdomain) {
        List<Word> words = queryService.findBySubdomain(subdomain);
        List<WordResponseDto> response = words.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search words")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> search(
            @RequestParam(required = false) String wordName,
            @RequestParam(required = false) String subdomain) {
        List<Word> words = queryService.search(wordName, subdomain);
        List<WordResponseDto> response = words.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create word")
    public ResponseEntity<ApiResponse<WordResponseDto>> create(@RequestBody Word word) {
        validator.validateForCreate(word.getWordName(), word.getSubdomain());
        Word saved = command.create(word);
        return ResponseEntity.ok(ApiResponse.success("Word created successfully", mapper.toResponse(saved)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update word")
    public ResponseEntity<ApiResponse<WordResponseDto>> update(@PathVariable Long id, @RequestBody Word word) {
        validator.validateForUpdate(id, word.getWordName(), word.getSubdomain());
        Word updated = command.update(id, word);
        return ResponseEntity.ok(ApiResponse.success("Word updated successfully", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete word")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        command.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Word deleted successfully", null));
    }
}
