package com.nhimex.assessment_collection.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String username;
    private String email;
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
    private List<String> roles;
    private Boolean status;
}
