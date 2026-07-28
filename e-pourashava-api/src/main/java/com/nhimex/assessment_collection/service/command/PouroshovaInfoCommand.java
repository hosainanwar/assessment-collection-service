package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.PouroshovaInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PouroshovaInfoCommand {

    private final PouroshovaInfoRepository pouroshovaInfoRepository;

    @Transactional
    public PouroshovaInfo create(PouroshovaInfo pouroshovaInfo) {
        if (pouroshovaInfoRepository.existsBySubdomain(pouroshovaInfo.getSubdomain())) {
            throw new UserInformException("Pouroshova info already exists for subdomain: " + pouroshovaInfo.getSubdomain());
        }
        return pouroshovaInfoRepository.save(pouroshovaInfo);
    }

    @Transactional
    public PouroshovaInfo update(Long id, PouroshovaInfo pouroshovaInfo) {
        PouroshovaInfo existing = pouroshovaInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PouroshovaInfo", "id", id));

        if (!existing.getSubdomain().equals(pouroshovaInfo.getSubdomain()) &&
                pouroshovaInfoRepository.existsBySubdomain(pouroshovaInfo.getSubdomain())) {
            throw new UserInformException("Subdomain already exists: " + pouroshovaInfo.getSubdomain());
        }

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
        existing.setSubdomain(pouroshovaInfo.getSubdomain());
        existing.setMayorLabelType(pouroshovaInfo.getMayorLabelType());
        existing.setMayorLabelTypeCollection(pouroshovaInfo.getMayorLabelTypeCollection());
        existing.setLogo(pouroshovaInfo.getLogo());
        existing.setMobile(pouroshovaInfo.getMobile());
        existing.setNirdharonMobile(pouroshovaInfo.getNirdharonMobile());

        return pouroshovaInfoRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        PouroshovaInfo pouroshovaInfo = pouroshovaInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PouroshovaInfo", "id", id));
        pouroshovaInfoRepository.delete(pouroshovaInfo);
    }
}
