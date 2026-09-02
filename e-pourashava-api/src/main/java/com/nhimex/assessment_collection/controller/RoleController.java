package com.nhimex.assessment_collection.controller;

import com.nhimex.assessment_collection.dto.request_dto.RoleRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.ApiResponse;
import com.nhimex.assessment_collection.dto.response_dto.PermissionResponseDto;
import com.nhimex.assessment_collection.dto.response_dto.RoleResponseDto;
import com.nhimex.assessment_collection.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role and permission management")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE:READ', 'USER:ASSIGN_ROLE', 'USER:CREATE')")
    @Operation(summary = "List roles")
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAll()));
    }

    @GetMapping("/assignable")
    @PreAuthorize("hasAnyAuthority('USER:ASSIGN_ROLE', 'USER:CREATE', 'USER:UPDATE')")
    @Operation(summary = "Roles the current user may assign")
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAssignable() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAssignable()));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAnyAuthority('ROLE:READ', 'ROLE:CREATE', 'ROLE:UPDATE')")
    @Operation(summary = "List all permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponseDto>>> getPermissions() {
        return ResponseEntity.ok(ApiResponse.success(roleService.findAllPermissions()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE:READ')")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE:CREATE')")
    @Operation(summary = "Create a role")
    public ResponseEntity<ApiResponse<RoleResponseDto>> create(@Valid @RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Role created successfully", roleService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE:UPDATE')")
    @Operation(summary = "Update a role")
    public ResponseEntity<ApiResponse<RoleResponseDto>> update(@PathVariable Long id,
                                                               @Valid @RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", roleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE:DELETE')")
    @Operation(summary = "Delete a role")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }
}
