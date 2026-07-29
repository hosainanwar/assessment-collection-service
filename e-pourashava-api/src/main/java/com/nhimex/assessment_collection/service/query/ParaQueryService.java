package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.ParaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParaQueryService {

    private final ParaRepository paraRepository;

    public List<Para> findAll() {
        return paraRepository.findAll();
    }

    public Para findById(Long id) {
        return paraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Para", "id", id));
    }

    public List<Para> findByWordId(Long wordId) {
        return paraRepository.findByWordId(wordId);
    }

    public List<Para> findBySubdomain(String subdomain) {
        return paraRepository.findBySubdomain(subdomain);
    }

    public List<Para> findByWordIdAndSubdomain(Long wordId, String subdomain) {
        return paraRepository.findByWordIdAndSubdomain(wordId, subdomain);
    }
}
