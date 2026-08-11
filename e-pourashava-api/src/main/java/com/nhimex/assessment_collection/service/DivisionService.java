package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.DivisionResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import com.nhimex.assessment_collection.service.command.DivisionCommand;
import com.nhimex.assessment_collection.service.mapper.DivisionMapper;
import com.nhimex.assessment_collection.service.query.DivisionQueryService;
import com.nhimex.assessment_collection.service.validator.DivisionValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DivisionService {

    private final DivisionQueryService queryService;
    private final DivisionCommand command;
    private final DivisionValidatorService validator;
    private final DivisionMapper mapper;

    public List<DivisionResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public DivisionResponseDto findById(Long id) {
        Division division = queryService.findById(id);
        return mapper.toResponse(division);
    }

    public DivisionResponseDto create(Division division) {
        validator.validateForCreate(division.getName());
        if (queryService.existsByName(division.getName())) {
            throw new UserInformException("Division name already exists: " + division.getName());
        }
        return mapper.toResponse(command.save(division));
    }

    public DivisionResponseDto update(Long id, Division division) {
        validator.validateForUpdate(id, division.getName());
        Division existing = queryService.findById(id);
        if (!existing.getName().equals(division.getName()) && queryService.existsByName(division.getName())) {
            throw new UserInformException("Division name already exists: " + division.getName());
        }
        existing.setName(division.getName());
        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        Division division = queryService.findById(id);
        command.delete(division);
    }
}
