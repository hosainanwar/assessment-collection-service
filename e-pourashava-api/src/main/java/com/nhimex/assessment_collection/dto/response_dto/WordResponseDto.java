package com.nhimex.assessment_collection.dto.response_dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordResponseDto {

    private Long id;
    private String wordName;
    private String subdomain;
    private String createdBy;
}
