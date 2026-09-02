package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
@Tag(name = "Words", description = "Word management endpoints")
public class WordController {

    private final WordService wordService;

    @GetMapping
    @PreAuthorize("hasAuthority('WORD:READ')")
    @Operation(summary = "Get all words")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> getAll() {
        List<WordResponseDto> words = wordService.findAll();
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('WORD:READ')")
    @Operation(summary = "Get word by ID")
    public ResponseEntity<ApiResponse<WordResponseDto>> getById(@PathVariable Long id) {
        WordResponseDto word = wordService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(word));
    }

    @GetMapping("/by-subdomain/{subdomain}")
    @PreAuthorize("hasAuthority('WORD:READ')")
    @Operation(summary = "Get words by subdomain")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> getBySubdomain(@PathVariable String subdomain) {
        List<WordResponseDto> words = wordService.findBySubdomain(subdomain);
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('WORD:READ')")
    @Operation(summary = "Search words")
    public ResponseEntity<ApiResponse<List<WordResponseDto>>> search(
            @RequestParam(required = false) String wordName,
            @RequestParam(required = false) String subdomain) {
        List<WordResponseDto> words = wordService.search(wordName, subdomain);
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WORD:CREATE')")
    @Operation(summary = "Create word")
    public ResponseEntity<ApiResponse<WordResponseDto>> create(@RequestBody Word word) {
        WordResponseDto created = wordService.create(word);
        return ResponseEntity.ok(ApiResponse.success("Word created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WORD:UPDATE')")
    @Operation(summary = "Update word")
    public ResponseEntity<ApiResponse<WordResponseDto>> update(@PathVariable Long id, @RequestBody Word word) {
        WordResponseDto updated = wordService.update(id, word);
        return ResponseEntity.ok(ApiResponse.success("Word updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WORD:DELETE')")
    @Operation(summary = "Delete word")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        wordService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Word deleted successfully", null));
    }
}
