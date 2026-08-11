package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.PouroshovaInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PouroshovaInfoQueryService {

    private final PouroshovaInfoRepository pouroshovaInfoRepository;

    public List<PouroshovaInfo> findAll() {
        return pouroshovaInfoRepository.findAll();
    }

    public PouroshovaInfo findById(Long id) {
        return pouroshovaInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PouroshovaInfo", "id", id));
    }

    public PouroshovaInfo findBySubdomain(String subdomain) {
        return pouroshovaInfoRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("PouroshovaInfo", "subdomain", subdomain));
    }

    public boolean existsBySubdomain(String subdomain) {
        return pouroshovaInfoRepository.existsBySubdomain(subdomain);
    }
}
