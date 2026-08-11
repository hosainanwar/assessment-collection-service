package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.WordResponseDto;
import com.nhimex.assessment_collection.entity.Word;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class WordMapper {

    public WordResponseDto toResponse(Word word) {
        if (word == null) {
            return null;
        }
        WordResponseDto response = new WordResponseDto();
        BeanUtils.copyProperties(word, response);
        return response;
    }
}
