package com.nhimex.assessment_collection.dto.response_dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String tenantId;
    private String role;
    private String subdomain;
}
