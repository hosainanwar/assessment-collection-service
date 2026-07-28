package com.nhimex.assessment_collection.dto.response_dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistrictResponseDto {

    private Long id;
    private String name;
    private String enName;
    private Long divisionId;
    private String divisionName;
}
