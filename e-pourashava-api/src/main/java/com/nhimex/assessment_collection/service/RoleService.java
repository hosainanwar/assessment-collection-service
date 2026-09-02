package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.request_dto.RoleRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.PermissionResponseDto;
import com.nhimex.assessment_collection.dto.response_dto.RoleResponseDto;
import com.nhimex.assessment_collection.entity.Permission;
import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.PermissionRepository;
import com.nhimex.assessment_collection.security.CurrentUserProvider;
import com.nhimex.assessment_collection.security.PermissionResolver;
import com.nhimex.assessment_collection.security.RoleCodes;
import com.nhimex.assessment_collection.security.UserPrincipal;
import com.nhimex.assessment_collection.service.command.RoleCommand;
import com.nhimex.assessment_collection.service.mapper.RoleMapper;
import com.nhimex.assessment_collection.service.query.RoleQueryService;
import com.nhimex.assessment_collection.service.validator.RoleValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleQueryService queryService;
    private final RoleCommand command;
    private final RoleValidatorService validator;
    private final RoleMapper mapper;
    private final PermissionRepository permissionRepository;
    private final PermissionResolver permissionResolver;

    public List<RoleResponseDto> findAll() {
        return queryService.findAllActive().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Roles the current user is allowed to assign: permission set must be a subset of theirs,
     * and SUPER_ADMIN is never assignable by anyone else.
     */
    public List<RoleResponseDto> findAssignable() {
        UserPrincipal actor = CurrentUserProvider.require();
        return queryService.findAllActive().stream()
                .filter(role -> canAssign(actor, role))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public RoleResponseDto findById(Long id) {
        return mapper.toResponse(queryService.findById(id));
    }

    public List<PermissionResponseDto> findAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscActionAsc().stream()
                .map(mapper::toPermission)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleResponseDto create(RoleRequestDto request) {
        String code = request.getCode().trim().toUpperCase();
        validator.validateForCreate(code);
        Role role = Role.builder()
                .code(code)
                .nameBn(request.getNameBn().trim())
                .nameEn(request.getNameEn().trim())
                .description(request.getDescription())
                .isSystem(false)
                .status(request.getStatus() != null ? request.getStatus() : true)
                .permissions(new HashSet<>(loadPermissions(request.getPermissionCodes())))
                .build();
        Role saved = command.save(role);
        permissionResolver.evictAll();
        return mapper.toResponse(queryService.findById(saved.getId()));
    }

    @Transactional
    public RoleResponseDto update(Long id, RoleRequestDto request) {
        Role existing = queryService.findById(id);
        validator.validateForUpdate(existing, request.getCode());
        if (!Boolean.TRUE.equals(existing.getIsSystem())) {
            existing.setCode(request.getCode().trim().toUpperCase());
        }
        existing.setNameBn(request.getNameBn().trim());
        existing.setNameEn(request.getNameEn().trim());
        existing.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        existing.getPermissions().clear();
        existing.getPermissions().addAll(loadPermissions(request.getPermissionCodes()));
        command.save(existing);
        permissionResolver.evictAll();
        return mapper.toResponse(queryService.findById(id));
    }

    @Transactional
    public void delete(Long id) {
        Role existing = queryService.findById(id);
        validator.validateForDelete(existing);
        command.delete(existing);
        permissionResolver.evictAll();
    }

    public void assertCanAssign(Iterable<Role> roles) {
        UserPrincipal actor = CurrentUserProvider.require();
        for (Role role : roles) {
            if (!canAssign(actor, role)) {
                throw new AccessDeniedException("Cannot assign role: " + role.getCode());
            }
        }
    }

    private boolean canAssign(UserPrincipal actor, Role role) {
        if (RoleCodes.SUPER_ADMIN.equals(role.getCode()) && !actor.isSuperAdmin()) {
            return false;
        }
        if (actor.isSuperAdmin()) {
            return true;
        }
        Set<String> actorPerms = actor.permissionSet();
        return role.getPermissions().stream()
                .map(Permission::getCode)
                .allMatch(actorPerms::contains);
    }

    private List<Permission> loadPermissions(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            throw new UserInformException("At least one permission is required");
        }
        List<String> normalized = codes.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .toList();
        List<Permission> permissions = permissionRepository.findByCodeIn(normalized);
        if (permissions.size() != normalized.size()) {
            throw new UserInformException("One or more permission codes are invalid");
        }
        return permissions;
    }
}
