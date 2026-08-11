package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistrictCommand {

    private final DistrictRepository districtRepository;

    @Transactional
    public District save(District district) {
        return districtRepository.save(district);
    }

    @Transactional
    public void delete(District district) {
        districtRepository.delete(district);
    }
}
