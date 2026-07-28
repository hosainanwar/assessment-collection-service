package com.nhimex.assessment_collection.dto.search_dto;

import lombok.Data;

@Data
public class DivisionSearchDto {

    private String name;
    private int page = 0;
    private int size = 20;
}
