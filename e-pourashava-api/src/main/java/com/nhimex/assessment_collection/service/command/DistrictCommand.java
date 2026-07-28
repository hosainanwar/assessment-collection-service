package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DistrictRepository;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistrictCommand {

    private final DistrictRepository districtRepository;
    private final DivisionRepository divisionRepository;

    @Transactional
    public District create(District district) {
        if (district.getDivision() == null || district.getDivision().getId() == null) {
            throw new UserInformException("Division is required");
        }

        divisionRepository.findById(district.getDivision().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", district.getDivision().getId()));

        if (districtRepository.existsByNameAndDivisionId(district.getName(), district.getDivision().getId())) {
            throw new UserInformException("District name already exists in this division: " + district.getName());
        }

        return districtRepository.save(district);
    }

    @Transactional
    public District update(Long id, District district) {
        District existing = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id));

        if (district.getDivision() != null && district.getDivision().getId() != null) {
            divisionRepository.findById(district.getDivision().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Division", "id", district.getDivision().getId()));
            existing.setDivision(district.getDivision());
        }

        if (!existing.getName().equals(district.getName()) &&
                districtRepository.existsByNameAndDivisionId(district.getName(), existing.getDivision().getId())) {
            throw new UserInformException("District name already exists in this division: " + district.getName());
        }

        existing.setName(district.getName());
        existing.setEnName(district.getEnName());
        return districtRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id));
        districtRepository.delete(district);
    }
}
