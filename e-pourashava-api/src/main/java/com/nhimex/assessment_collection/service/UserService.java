package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.request_dto.UserRequestDto;
import com.nhimex.assessment_collection.dto.response_dto.UserResponseDto;
import com.nhimex.assessment_collection.entity.User;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.service.command.UserCommand;
import com.nhimex.assessment_collection.service.mapper.UserMapper;
import com.nhimex.assessment_collection.service.query.UserQueryService;
import com.nhimex.assessment_collection.service.validator.UserValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserQueryService queryService;
    private final UserCommand command;
    private final UserValidatorService validator;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

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

    public UserResponseDto create(UserRequestDto request) {
        validator.validateForCreate(
                request.getName(), request.getUsername(), request.getEmail(),
                request.getPassword(), request.getSubdomain());

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
                .subdomain(request.getSubdomain())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .status(request.getStatus() != null ? request.getStatus() : true)
                .build();

        return mapper.toResponse(command.save(user));
    }

    public UserResponseDto update(Long id, UserRequestDto request) {
        validator.validateForUpdate(
                id, request.getName(), request.getUsername(),
                request.getEmail(), request.getSubdomain());

        User existing = queryService.findById(id);
        existing.setName(request.getName());
        existing.setDepartment(request.getDepartment());
        existing.setDesignation(request.getDesignation());
        existing.setAddress(request.getAddress());
        existing.setDivision(request.getDivision());
        existing.setDistrict(request.getDistrict());
        existing.setPostalCode(request.getPostalCode());
        existing.setMobileNo(request.getMobileNo());
        existing.setSubdomain(request.getSubdomain());
        if (request.getRole() != null) existing.setRole(request.getRole());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        User user = queryService.findById(id);
        command.delete(user);
    }
}
