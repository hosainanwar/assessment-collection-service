package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Word;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    public WordResponseDto toResponse(Word word) {
        if (word == null) {
            return null;
        }
        return WordResponseDto.builder()
                .id(word.getId())
                .wordName(word.getWordName())
                .subdomain(word.getSubdomain())
                .createdBy(word.getCreatedBy())
                .build();
    }
}
