package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.PourashavaResponseDto;
import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import com.nhimex.assessment_collection.repository.DistrictRepository;
import com.nhimex.assessment_collection.service.command.PourashavaCommand;
import com.nhimex.assessment_collection.service.mapper.PourashavaMapper;
import com.nhimex.assessment_collection.service.query.PourashavaQueryService;
import com.nhimex.assessment_collection.service.validator.PourashavaValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PourashavaService {

    private final PourashavaQueryService queryService;
    private final PourashavaCommand command;
    private final PourashavaValidatorService validator;
    private final PourashavaMapper mapper;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;

    public List<PourashavaResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PourashavaResponseDto findById(Long id) {
        Pourashava pourashava = queryService.findById(id);
        return mapper.toResponse(pourashava);
    }

    public PourashavaResponseDto findBySubdomain(String subdomain) {
        Pourashava pourashava = queryService.findBySubdomain(subdomain);
        return mapper.toResponse(pourashava);
    }

    public PourashavaResponseDto create(Pourashava pourashava) {
        if (pourashava.getDivision() == null || pourashava.getDivision().getId() == null) {
            throw new UserInformException("Division is required");
        }
        if (pourashava.getDistrict() == null || pourashava.getDistrict().getId() == null) {
            throw new UserInformException("District is required");
        }

        Division division = divisionRepository.findById(pourashava.getDivision().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", pourashava.getDivision().getId()));
        District district = districtRepository.findById(pourashava.getDistrict().getId())
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", pourashava.getDistrict().getId()));

        pourashava.setDivision(division);
        pourashava.setDistrict(district);

        validator.validateForCreate(pourashava.getBnName(), pourashava.getEnName(), pourashava.getSubdomain(),
                pourashava.getDivision().getId(), pourashava.getDistrict().getId());
        if (queryService.existsBySubdomain(pourashava.getSubdomain())) {
            throw new UserInformException("Subdomain already exists: " + pourashava.getSubdomain());
        }

        return mapper.toResponse(command.save(pourashava));
    }

    public PourashavaResponseDto update(Long id, Pourashava pourashava) {
        Pourashava existing = queryService.findById(id);

        if (pourashava.getDivision() != null && pourashava.getDivision().getId() != null) {
            Division division = divisionRepository.findById(pourashava.getDivision().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Division", "id", pourashava.getDivision().getId()));
            existing.setDivision(division);
        }

        if (pourashava.getDistrict() != null && pourashava.getDistrict().getId() != null) {
            District district = districtRepository.findById(pourashava.getDistrict().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("District", "id", pourashava.getDistrict().getId()));
            existing.setDistrict(district);
        }

        if (!existing.getSubdomain().equals(pourashava.getSubdomain()) &&
                queryService.existsBySubdomain(pourashava.getSubdomain())) {
            throw new UserInformException("Subdomain already exists: " + pourashava.getSubdomain());
        }

        existing.setBnName(pourashava.getBnName());
        existing.setEnName(pourashava.getEnName());
        existing.setSubdomain(pourashava.getSubdomain());
        existing.setFeatures(pourashava.getFeatures());
        existing.setIpAddress(pourashava.getIpAddress());

        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        Pourashava pourashava = queryService.findById(id);
        command.delete(pourashava);
    }
}
