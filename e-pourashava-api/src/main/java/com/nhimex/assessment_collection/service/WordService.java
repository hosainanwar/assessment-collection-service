package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.security.TenantGuard;
import com.nhimex.assessment_collection.service.command.WordCommand;
import com.nhimex.assessment_collection.service.mapper.WordMapper;
import com.nhimex.assessment_collection.service.query.WordQueryService;
import com.nhimex.assessment_collection.service.validator.WordValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordQueryService queryService;
    private final WordCommand command;
    private final WordValidatorService validator;
    private final WordMapper mapper;
    private final TenantGuard tenantGuard;

    public List<WordResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public WordResponseDto findById(Long id) {
        Word word = queryService.findById(id);
        return mapper.toResponse(word);
    }

    public List<WordResponseDto> findBySubdomain(String subdomain) {
        return queryService.findBySubdomain(subdomain).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WordResponseDto> search(String wordName, String subdomain) {
        return queryService.search(wordName, subdomain).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public WordResponseDto create(Word word) {
        Pourashava pourashava = tenantGuard.resolvePourashava(
                word.getPourashava() != null ? word.getPourashava().getId() : null,
                word.getSubdomain());
        word.setPourashava(pourashava);
        word.setSubdomain(pourashava.getSubdomain());
        validator.validateForCreate(word.getWordName(), word.getSubdomain());
        return mapper.toResponse(command.save(word));
    }

    public WordResponseDto update(Long id, Word word) {
        Word existing = queryService.findById(id);
        tenantGuard.assertSameTenant(existing.getPourashava().getId());
        validator.validateForUpdate(id, word.getWordName(), existing.getSubdomain());
        existing.setWordName(word.getWordName());
        existing.setCreatedBy(word.getCreatedBy());
        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        Word word = queryService.findById(id);
        tenantGuard.assertSameTenant(word.getPourashava().getId());
        command.delete(word);
    }
}
