package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.DivisionResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DivisionMapper {

    public DivisionResponseDto toResponse(Division division) {
        if (division == null) {
            return null;
        }
        DivisionResponseDto response = new DivisionResponseDto();
        BeanUtils.copyProperties(division, response);
        return response;
    }
}
