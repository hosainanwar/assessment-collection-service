package com.nhimex.assessment_collection.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequestDto {

    @NotBlank(message = "Role code is required")
    private String code;

    @NotBlank(message = "Bengali name is required")
    private String nameBn;

    @NotBlank(message = "English name is required")
    private String nameEn;

    private String description;

    private Boolean status;

    @NotEmpty(message = "At least one permission is required")
    private List<String> permissionCodes;
}
