package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.PourashavaResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import org.springframework.stereotype.Component;

@Component
public class PourashavaMapper {

    public PourashavaResponseDto toResponse(Pourashava pourashava) {
        if (pourashava == null) {
            return null;
        }
        return PourashavaResponseDto.builder()
                .id(pourashava.getId())
                .bnName(pourashava.getBnName())
                .enName(pourashava.getEnName())
                .subdomain(pourashava.getSubdomain())
                .features(pourashava.getFeatures())
                .divisionId(pourashava.getDivision() != null ? pourashava.getDivision().getId() : null)
                .divisionName(pourashava.getDivision() != null ? pourashava.getDivision().getName() : null)
                .districtId(pourashava.getDistrict() != null ? pourashava.getDistrict().getId() : null)
                .districtName(pourashava.getDistrict() != null ? pourashava.getDistrict().getName() : null)
                .ipAddress(pourashava.getIpAddress())
                .build();
    }
}
