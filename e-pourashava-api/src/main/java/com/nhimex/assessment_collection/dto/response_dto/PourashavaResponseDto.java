package com.nhimex.assessment_collection.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PourashavaResponseDto {

    private Long id;
    private String bnName;
    private String enName;
    private String subdomain;
    private String features;
    private Long divisionId;
    private String divisionName;
    private Long districtId;
    private String districtName;
    private String ipAddress;
}
