package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.ParaResponseDto;
import com.nhimex.assessment_collection.entity.Para;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ParaMapper {

    public ParaResponseDto toResponse(Para para) {
        if (para == null) {
            return null;
        }
        ParaResponseDto response = new ParaResponseDto();
        BeanUtils.copyProperties(para, response);
        if (para.getWord() != null) {
            response.setWordId(para.getWord().getId());
            response.setWordName(para.getWord().getWordName());
        }
        return response;
    }
}
