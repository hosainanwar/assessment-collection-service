package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.PouroshovaInfoResponseDto;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.security.TenantGuard;
import com.nhimex.assessment_collection.service.command.PouroshovaInfoCommand;
import com.nhimex.assessment_collection.service.mapper.PouroshovaInfoMapper;
import com.nhimex.assessment_collection.service.query.PouroshovaInfoQueryService;
import com.nhimex.assessment_collection.service.validator.PouroshovaInfoValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PouroshovaInfoService {

    private final PouroshovaInfoQueryService queryService;
    private final PouroshovaInfoCommand command;
    private final PouroshovaInfoValidatorService validator;
    private final PouroshovaInfoMapper mapper;
    private final TenantGuard tenantGuard;

    public List<PouroshovaInfoResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PouroshovaInfoResponseDto findById(Long id) {
        PouroshovaInfo pouroshovaInfo = queryService.findById(id);
        return mapper.toResponse(pouroshovaInfo);
    }

    public PouroshovaInfoResponseDto findBySubdomain(String subdomain) {
        PouroshovaInfo pouroshovaInfo = queryService.findBySubdomain(subdomain);
        return mapper.toResponse(pouroshovaInfo);
    }

    public PouroshovaInfoResponseDto create(PouroshovaInfo pouroshovaInfo) {
        Pourashava pourashava = tenantGuard.resolvePourashava(
                pouroshovaInfo.getPourashava() != null ? pouroshovaInfo.getPourashava().getId() : null,
                pouroshovaInfo.getSubdomain());
        pouroshovaInfo.setPourashava(pourashava);
        pouroshovaInfo.setSubdomain(pourashava.getSubdomain());
        validator.validateForCreate(pouroshovaInfo.getPouroshovaName(), pouroshovaInfo.getMeyorName(),
                pouroshovaInfo.getPsName(), pouroshovaInfo.getDsName(),
                pouroshovaInfo.getSignatureName(), pouroshovaInfo.getSubdomain());
        if (queryService.existsBySubdomain(pouroshovaInfo.getSubdomain())) {
            throw new UserInformException("Pouroshova info already exists for subdomain: " + pouroshovaInfo.getSubdomain());
        }
        return mapper.toResponse(command.save(pouroshovaInfo));
    }

    public PouroshovaInfoResponseDto update(Long id, PouroshovaInfo pouroshovaInfo) {
        PouroshovaInfo existing = queryService.findById(id);
        tenantGuard.assertSameTenant(existing.getPourashava().getId());

        existing.setPouroshovaName(pouroshovaInfo.getPouroshovaName());
        existing.setMeyorName(pouroshovaInfo.getMeyorName());
        existing.setKorNirdharokName(pouroshovaInfo.getKorNirdharokName());
        existing.setPsName(pouroshovaInfo.getPsName());
        existing.setDsName(pouroshovaInfo.getDsName());
        existing.setSignatureName(pouroshovaInfo.getSignatureName());
        existing.setMayorSign(pouroshovaInfo.getMayorSign());
        existing.setAssessorSign(pouroshovaInfo.getAssessorSign());
        existing.setTaxCollectorType(pouroshovaInfo.getTaxCollectorType());
        existing.setTaxCollectorSign(pouroshovaInfo.getTaxCollectorSign());
        existing.setMayorLabelType(pouroshovaInfo.getMayorLabelType());
        existing.setMayorLabelTypeCollection(pouroshovaInfo.getMayorLabelTypeCollection());
        existing.setLogo(pouroshovaInfo.getLogo());
        existing.setMobile(pouroshovaInfo.getMobile());
        existing.setNirdharonMobile(pouroshovaInfo.getNirdharonMobile());

        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        PouroshovaInfo pouroshovaInfo = queryService.findById(id);
        tenantGuard.assertSameTenant(pouroshovaInfo.getPourashava().getId());
        command.delete(pouroshovaInfo);
    }
}
