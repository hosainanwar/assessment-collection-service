package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.DistrictResponseDto;
import com.nhimex.assessment_collection.entity.District;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DistrictMapper {

    public DistrictResponseDto toResponse(District district) {
        if (district == null) {
            return null;
        }
        DistrictResponseDto response = new DistrictResponseDto();
        BeanUtils.copyProperties(district, response);
        if (district.getDivision() != null) {
            response.setDivisionId(district.getDivision().getId());
            response.setDivisionName(district.getDivision().getName());
        }
        return response;
    }
}
