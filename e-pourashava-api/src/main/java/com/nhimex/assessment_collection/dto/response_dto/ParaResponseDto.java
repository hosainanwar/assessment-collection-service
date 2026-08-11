package com.nhimex.assessment_collection.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParaResponseDto {

    private Long id;
    private String pbrName;
    private Long wordId;
    private String wordName;
    private String subdomain;
    private String createdBy;
}
