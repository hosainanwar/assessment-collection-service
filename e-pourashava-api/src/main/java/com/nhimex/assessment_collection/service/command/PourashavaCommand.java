package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DistrictRepository;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import com.nhimex.assessment_collection.repository.PourashavaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PourashavaCommand {

    private final PourashavaRepository pourashavaRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictRepository districtRepository;

    @Transactional
    public Pourashava create(Pourashava pourashava) {
        if (pourashava.getDivision() == null || pourashava.getDivision().getId() == null) {
            throw new UserInformException("Division is required");
        }
        if (pourashava.getDistrict() == null || pourashava.getDistrict().getId() == null) {
            throw new UserInformException("District is required");
        }

        divisionRepository.findById(pourashava.getDivision().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", pourashava.getDivision().getId()));
        districtRepository.findById(pourashava.getDistrict().getId())
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", pourashava.getDistrict().getId()));

        if (pourashavaRepository.existsBySubdomain(pourashava.getSubdomain())) {
            throw new UserInformException("Subdomain already exists: " + pourashava.getSubdomain());
        }

        return pourashavaRepository.save(pourashava);
    }

    @Transactional
    public Pourashava update(Long id, Pourashava pourashava) {
        Pourashava existing = pourashavaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "id", id));

        if (!existing.getSubdomain().equals(pourashava.getSubdomain()) &&
                pourashavaRepository.existsBySubdomain(pourashava.getSubdomain())) {
            throw new UserInformException("Subdomain already exists: " + pourashava.getSubdomain());
        }

        if (pourashava.getDivision() != null && pourashava.getDivision().getId() != null) {
            divisionRepository.findById(pourashava.getDivision().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Division", "id", pourashava.getDivision().getId()));
            existing.setDivision(pourashava.getDivision());
        }

        if (pourashava.getDistrict() != null && pourashava.getDistrict().getId() != null) {
            districtRepository.findById(pourashava.getDistrict().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("District", "id", pourashava.getDistrict().getId()));
            existing.setDistrict(pourashava.getDistrict());
        }

        existing.setBnName(pourashava.getBnName());
        existing.setEnName(pourashava.getEnName());
        existing.setSubdomain(pourashava.getSubdomain());
        existing.setFeatures(pourashava.getFeatures());
        existing.setIpAddress(pourashava.getIpAddress());

        return pourashavaRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Pourashava pourashava = pourashavaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "id", id));
        pourashavaRepository.delete(pourashava);
    }
}
