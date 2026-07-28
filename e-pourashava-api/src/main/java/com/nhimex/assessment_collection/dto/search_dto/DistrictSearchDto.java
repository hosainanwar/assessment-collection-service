package com.nhimex.assessment_collection.dto.search_dto;

import lombok.Data;

@Data
public class DistrictSearchDto {

    private String name;
    private Long divisionId;
    private int page = 0;
    private int size = 20;
}
