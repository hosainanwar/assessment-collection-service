package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.request_dto.UserRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.UserResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.entity.User;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.RoleRepository;
import com.nhimex.assessment_collection.security.PermissionResolver;
import com.nhimex.assessment_collection.security.RoleCodes;
import com.nhimex.assessment_collection.security.TenantGuard;
import com.nhimex.assessment_collection.service.command.UserCommand;
import com.nhimex.assessment_collection.service.mapper.UserMapper;
import com.nhimex.assessment_collection.service.query.UserQueryService;
import com.nhimex.assessment_collection.service.validator.UserValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserQueryService queryService;
    private final UserCommand command;
    private final UserValidatorService validator;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final TenantGuard tenantGuard;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PermissionResolver permissionResolver;

    public List<UserResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDto findById(Long id) {
        User user = queryService.findById(id);
        return mapper.toResponse(user);
    }

    public List<UserResponseDto> search(String name, String subdomain) {
        return queryService.search(name, subdomain).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto create(UserRequestDto request) {
        validator.validateForCreate(
                request.getName(), request.getUsername(), request.getEmail(),
                request.getPassword(), request.getSubdomain());

        Pourashava pourashava = tenantGuard.resolvePourashava(request.getPourashavaId(), request.getSubdomain());
        Set<Role> roles = resolveRoles(request);

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .address(request.getAddress())
                .division(request.getDivision())
                .district(request.getDistrict())
                .postalCode(request.getPostalCode())
                .mobileNo(request.getMobileNo())
                .subdomain(pourashava.getSubdomain())
                .pourashava(pourashava)
                .roles(roles)
                .status(request.getStatus() != null ? request.getStatus() : true)
                .build();

        User saved = command.save(user);
        permissionResolver.evictUser(saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto request) {
        validator.validateForUpdate(
                id, request.getName(), request.getUsername(),
                request.getEmail(), request.getSubdomain());

        User existing = queryService.findById(id);
        tenantGuard.assertSameTenant(existing.getPourashava().getId());

        existing.setName(request.getName());
        existing.setDepartment(request.getDepartment());
        existing.setDesignation(request.getDesignation());
        existing.setAddress(request.getAddress());
        existing.setDivision(request.getDivision());
        existing.setDistrict(request.getDistrict());
        existing.setPostalCode(request.getPostalCode());
        existing.setMobileNo(request.getMobileNo());
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (hasRolePayload(request)) {
            existing.getRoles().clear();
            existing.getRoles().addAll(resolveRoles(request));
        }

        User saved = command.save(existing);
        permissionResolver.evictUser(saved.getId());
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        User user = queryService.findById(id);
        tenantGuard.assertSameTenant(user.getPourashava().getId());
        permissionResolver.evictUser(user.getId());
        command.delete(user);
    }

    private boolean hasRolePayload(UserRequestDto request) {
        return (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty())
                || StringUtils.hasText(request.getRole());
    }

    private Set<Role> resolveRoles(UserRequestDto request) {
        List<String> codes = new ArrayList<>();
        if (request.getRoleCodes() != null) {
            request.getRoleCodes().stream()
                    .filter(StringUtils::hasText)
                    .map(RoleCodes::fromLegacy)
                    .forEach(codes::add);
        }
        if (codes.isEmpty() && StringUtils.hasText(request.getRole())) {
            codes.add(RoleCodes.fromLegacy(request.getRole()));
        }
        if (codes.isEmpty()) {
            codes.add(RoleCodes.VIEWER);
        }
        List<String> distinct = codes.stream().distinct().toList();
        List<Role> roles = roleRepository.findByCodeIn(distinct);
        if (roles.size() != distinct.size()) {
            throw new UserInformException("One or more role codes are invalid");
        }
        roleService.assertCanAssign(roles);
        return new HashSet<>(roles);
    }
}
