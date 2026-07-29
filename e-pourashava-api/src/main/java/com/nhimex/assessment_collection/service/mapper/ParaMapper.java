package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.ParaResponseDto;
import com.nhimex.assessment_collection.entity.Para;
import org.springframework.stereotype.Component;

@Component
public class ParaMapper {

    public ParaResponseDto toResponse(Para para) {
        if (para == null) {
            return null;
        }
        return ParaResponseDto.builder()
                .id(para.getId())
                .pbrName(para.getPbrName())
                .wordId(para.getWord() != null ? para.getWord().getId() : null)
                .wordName(para.getWord() != null ? para.getWord().getWordName() : null)
                .subdomain(para.getSubdomain())
                .createdBy(para.getCreatedBy())
                .build();
    }
}
