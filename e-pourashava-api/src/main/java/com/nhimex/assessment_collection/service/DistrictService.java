package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.DistrictResponseDto;
import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import com.nhimex.assessment_collection.service.command.DistrictCommand;
import com.nhimex.assessment_collection.service.mapper.DistrictMapper;
import com.nhimex.assessment_collection.service.query.DistrictQueryService;
import com.nhimex.assessment_collection.service.validator.DistrictValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictQueryService queryService;
    private final DistrictCommand command;
    private final DistrictValidatorService validator;
    private final DistrictMapper mapper;
    private final DivisionRepository divisionRepository;

    public List<DistrictResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public DistrictResponseDto findById(Long id) {
        District district = queryService.findById(id);
        return mapper.toResponse(district);
    }

    public List<DistrictResponseDto> findByDivisionId(Long divisionId) {
        return queryService.findByDivisionId(divisionId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public DistrictResponseDto create(District district) {
        if (district.getDivision() == null || district.getDivision().getId() == null) {
            throw new UserInformException("Division is required");
        }
        Division division = divisionRepository.findById(district.getDivision().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", district.getDivision().getId()));
        district.setDivision(division);

        validator.validateForCreate(district.getName(), district.getEnName(), district.getDivision().getId());
        if (queryService.existsByNameAndDivisionId(district.getName(), district.getDivision().getId())) {
            throw new UserInformException("District name already exists in this division: " + district.getName());
        }
        return mapper.toResponse(command.save(district));
    }

    public DistrictResponseDto update(Long id, District district) {
        District existing = queryService.findById(id);

        if (district.getDivision() != null && district.getDivision().getId() != null) {
            Division division = divisionRepository.findById(district.getDivision().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Division", "id", district.getDivision().getId()));
            existing.setDivision(division);
        }

        validator.validateForUpdate(id, district.getName(), district.getEnName(),
                existing.getDivision() != null ? existing.getDivision().getId() : null);

        if (!existing.getName().equals(district.getName()) &&
                queryService.existsByNameAndDivisionId(district.getName(), existing.getDivision().getId())) {
            throw new UserInformException("District name already exists in this division: " + district.getName());
        }

        existing.setName(district.getName());
        existing.setEnName(district.getEnName());
        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        District district = queryService.findById(id);
        command.delete(district);
    }
}
