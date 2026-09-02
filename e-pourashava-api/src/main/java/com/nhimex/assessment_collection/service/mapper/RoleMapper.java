package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.PermissionResponseDto;
import com.nhimex.assessment_collection.dto.response_dto.RoleResponseDto;
import com.nhimex.assessment_collection.entity.Permission;
import com.nhimex.assessment_collection.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RoleMapper {

    public RoleResponseDto toResponse(Role role) {
        if (role == null) {
            return null;
        }
        return RoleResponseDto.builder()
                .id(role.getId())
                .code(role.getCode())
                .nameBn(role.getNameBn())
                .nameEn(role.getNameEn())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .status(role.getStatus())
                .permissions(role.getPermissions() == null ? List.of() : role.getPermissions().stream()
                        .sorted(Comparator.comparing(Permission::getCode))
                        .map(this::toPermission)
                        .toList())
                .build();
    }

    public PermissionResponseDto toPermission(Permission permission) {
        if (permission == null) {
            return null;
        }
        return PermissionResponseDto.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .module(permission.getModule())
                .action(permission.getAction())
                .description(permission.getDescription())
                .build();
    }
}
