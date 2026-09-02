package com.nhimex.assessment_collection.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {

    private Long id;
    private String code;
    private String module;
    private String action;
    private String description;
}
