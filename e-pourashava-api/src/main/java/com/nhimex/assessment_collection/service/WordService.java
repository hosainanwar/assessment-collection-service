package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Word;
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
        validator.validateForCreate(word.getWordName(), word.getSubdomain());
        return mapper.toResponse(command.save(word));
    }

    public WordResponseDto update(Long id, Word word) {
        validator.validateForUpdate(id, word.getWordName(), word.getSubdomain());
        Word existing = queryService.findById(id);
        existing.setWordName(word.getWordName());
        existing.setSubdomain(word.getSubdomain());
        existing.setCreatedBy(word.getCreatedBy());
        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        Word word = queryService.findById(id);
        command.delete(word);
    }
}
