package com.nhimex.assessment_collection.dto.request_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String password;

    private String department;

    private String designation;

    private String address;

    private String division;

    private String district;

    private String postalCode;

    private String mobileNo;

    private String subdomain;

    private Long pourashavaId;

    private String role;

    private List<String> roleCodes;

    private Boolean status;
}
