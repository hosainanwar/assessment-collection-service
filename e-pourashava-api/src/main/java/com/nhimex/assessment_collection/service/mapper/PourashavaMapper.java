package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.PourashavaResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PourashavaMapper {

    public PourashavaResponseDto toResponse(Pourashava pourashava) {
        if (pourashava == null) {
            return null;
        }
        PourashavaResponseDto response = new PourashavaResponseDto();
        BeanUtils.copyProperties(pourashava, response);
        if (pourashava.getDivision() != null) {
            response.setDivisionId(pourashava.getDivision().getId());
            response.setDivisionName(pourashava.getDivision().getName());
        }
        if (pourashava.getDistrict() != null) {
            response.setDistrictId(pourashava.getDistrict().getId());
            response.setDistrictName(pourashava.getDistrict().getName());
        }
        return response;
    }
}
