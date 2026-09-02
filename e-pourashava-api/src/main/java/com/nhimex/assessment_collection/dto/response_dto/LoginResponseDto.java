package com.nhimex.assessment_collection.dto.response_dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String tenantId;
    private Long pourashavaId;
    private String role;
    private List<String> roles;
    private List<String> permissions;
    private String subdomain;
}
