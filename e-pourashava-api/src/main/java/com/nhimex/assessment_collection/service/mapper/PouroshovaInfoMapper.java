package com.nhimex.assessment_collection.service.mapper;

import com.nhimex.assessment_collection.dto.response_dto.PouroshovaInfoResponseDto;
import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import org.springframework.stereotype.Component;

@Component
public class PouroshovaInfoMapper {

    public PouroshovaInfoResponseDto toResponse(PouroshovaInfo pouroshovaInfo) {
        if (pouroshovaInfo == null) {
            return null;
        }
        return PouroshovaInfoResponseDto.builder()
                .id(pouroshovaInfo.getId())
                .pouroshovaName(pouroshovaInfo.getPouroshovaName())
                .meyorName(pouroshovaInfo.getMeyorName())
                .korNirdharokName(pouroshovaInfo.getKorNirdharokName())
                .psName(pouroshovaInfo.getPsName())
                .dsName(pouroshovaInfo.getDsName())
                .signatureName(pouroshovaInfo.getSignatureName())
                .mayorSign(pouroshovaInfo.getMayorSign())
                .assessorSign(pouroshovaInfo.getAssessorSign())
                .taxCollectorType(pouroshovaInfo.getTaxCollectorType())
                .taxCollectorSign(pouroshovaInfo.getTaxCollectorSign())
                .subdomain(pouroshovaInfo.getSubdomain())
                .mayorLabelType(pouroshovaInfo.getMayorLabelType())
                .mayorLabelTypeCollection(pouroshovaInfo.getMayorLabelTypeCollection())
                .logo(pouroshovaInfo.getLogo())
                .mobile(pouroshovaInfo.getMobile())
                .nirdharonMobile(pouroshovaInfo.getNirdharonMobile())
                .build();
    }
}
