package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.District;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictQueryService {

    private final DistrictRepository districtRepository;

    public List<District> findAll() {
        return districtRepository.findAll();
    }

    public District findById(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id));
    }

    public List<District> findByDivisionId(Long divisionId) {
        return districtRepository.findByDivisionId(divisionId);
    }

    public Page<District> search(String name, Long divisionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<District> spec = (root, query, cb) -> {
            if (name != null && !name.isEmpty()) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return null;
        };
        if (divisionId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("division").get("id"), divisionId));
        }
        return districtRepository.findAll(spec, pageable);
    }

    public boolean existsByNameAndDivisionId(String name, Long divisionId) {
        return districtRepository.existsByNameAndDivisionId(name, divisionId);
    }
}
