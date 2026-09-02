package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.UserResponseDto;
import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }
        List<String> roles = user.getRoles() == null ? List.of() : user.getRoles().stream()
                .map(Role::getCode)
                .toList();
        Long pourashavaId = user.getPourashava() != null ? user.getPourashava().getId() : null;
        String subdomain = user.getPourashava() != null ? user.getPourashava().getSubdomain() : user.getSubdomain();
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .address(user.getAddress())
                .division(user.getDivision())
                .district(user.getDistrict())
                .postalCode(user.getPostalCode())
                .mobileNo(user.getMobileNo())
                .subdomain(subdomain)
                .pourashavaId(pourashavaId)
                .role(roles.isEmpty() ? null : roles.get(0))
                .roles(roles)
                .status(user.getStatus())
                .build();
    }
}
