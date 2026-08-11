package com.nhimex.assessment_collection.dto.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PouroshovaInfoResponseDto {

    private Long id;
    private String pouroshovaName;
    private String meyorName;
    private String korNirdharokName;
    private String psName;
    private String dsName;
    private String signatureName;
    private String mayorSign;
    private String assessorSign;
    private String taxCollectorType;
    private String taxCollectorSign;
    private String subdomain;
    private String mayorLabelType;
    private String mayorLabelTypeCollection;
    private String logo;
    private String mobile;
    private String nirdharonMobile;
}
