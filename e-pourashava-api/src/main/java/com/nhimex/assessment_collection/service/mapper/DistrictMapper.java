package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.DistrictResponseDto;
import com.nhimex.assessment_collection.entity.District;
import org.springframework.stereotype.Component;

@Component
public class DistrictMapper {

    public DistrictResponseDto toResponse(District district) {
        if (district == null) {
            return null;
        }
        return DistrictResponseDto.builder()
                .id(district.getId())
                .name(district.getName())
                .enName(district.getEnName())
                .divisionId(district.getDivision() != null ? district.getDivision().getId() : null)
                .divisionName(district.getDivision() != null ? district.getDivision().getName() : null)
                .build();
    }
}
