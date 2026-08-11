package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.PourashavaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PourashavaQueryService {

    private final PourashavaRepository pourashavaRepository;

    public List<Pourashava> findAll() {
        return pourashavaRepository.findAll();
    }

    public Pourashava findById(Long id) {
        return pourashavaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "id", id));
    }

    public Pourashava findBySubdomain(String subdomain) {
        return pourashavaRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "subdomain", subdomain));
    }

    public List<Pourashava> findByDistrictId(Long districtId) {
        return pourashavaRepository.findByDistrictId(districtId);
    }

    public List<Pourashava> findByDivisionId(Long divisionId) {
        return pourashavaRepository.findByDivisionId(divisionId);
    }

    public boolean existsBySubdomain(String subdomain) {
        return pourashavaRepository.existsBySubdomain(subdomain);
    }
}
