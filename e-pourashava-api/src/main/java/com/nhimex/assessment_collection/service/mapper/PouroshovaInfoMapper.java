package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.PouroshovaInfoResponseDto;
import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PouroshovaInfoMapper {

    public PouroshovaInfoResponseDto toResponse(PouroshovaInfo pouroshovaInfo) {
        if (pouroshovaInfo == null) {
            return null;
        }
        PouroshovaInfoResponseDto response = new PouroshovaInfoResponseDto();
        BeanUtils.copyProperties(pouroshovaInfo, response);
        return response;
    }
}
