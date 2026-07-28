package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.DivisionResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import org.springframework.stereotype.Component;

@Component
public class DivisionMapper {

    public DivisionResponseDto toResponse(Division division) {
        if (division == null) {
            return null;
        }
        return DivisionResponseDto.builder()
                .id(division.getId())
                .name(division.getName())
                .build();
    }
}
