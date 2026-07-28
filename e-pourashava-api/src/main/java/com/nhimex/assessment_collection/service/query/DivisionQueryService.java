package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionQueryService {

    private final DivisionRepository divisionRepository;

    public List<Division> findAll() {
        return divisionRepository.findAll();
    }

    public Division findById(Long id) {
        return divisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", id));
    }

    public Division findByName(String name) {
        return divisionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Division", "name", name));
    }

    public Page<Division> search(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Division> spec = (root, query, cb) -> {
            if (name != null && !name.isEmpty()) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return null;
        };
        return divisionRepository.findAll(spec, pageable);
    }
}
