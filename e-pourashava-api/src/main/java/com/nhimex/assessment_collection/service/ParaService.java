package com.nhimex.assessment_collection.service;

import com.nhimex.assessment_collection.dto.response_dto.ParaResponseDto;
import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.WordRepository;
import com.nhimex.assessment_collection.security.TenantGuard;
import com.nhimex.assessment_collection.service.command.ParaCommand;
import com.nhimex.assessment_collection.service.mapper.ParaMapper;
import com.nhimex.assessment_collection.service.query.ParaQueryService;
import com.nhimex.assessment_collection.service.validator.ParaValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParaService {

    private final ParaQueryService queryService;
    private final ParaCommand command;
    private final ParaValidatorService validator;
    private final ParaMapper mapper;
    private final WordRepository wordRepository;
    private final TenantGuard tenantGuard;

    public List<ParaResponseDto> findAll() {
        return queryService.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public ParaResponseDto findById(Long id) {
        Para para = queryService.findById(id);
        return mapper.toResponse(para);
    }

    public List<ParaResponseDto> findByWordId(Long wordId) {
        return queryService.findByWordId(wordId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ParaResponseDto> findBySubdomain(String subdomain) {
        return queryService.findBySubdomain(subdomain).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ParaResponseDto> findByWordIdAndSubdomain(Long wordId, String subdomain) {
        return queryService.findByWordIdAndSubdomain(wordId, subdomain).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public ParaResponseDto create(Para para) {
        if (para.getWord() != null && para.getWord().getId() != null) {
            Word word = wordRepository.findById(para.getWord().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Word", "id", para.getWord().getId()));
            para.setWord(word);
        }
        Pourashava pourashava = tenantGuard.resolvePourashava(
                para.getPourashava() != null ? para.getPourashava().getId() : null,
                para.getSubdomain());
        para.setPourashava(pourashava);
        para.setSubdomain(pourashava.getSubdomain());
        validator.validateForCreate(para.getPbrName(),
                para.getWord() != null ? para.getWord().getId() : null,
                para.getSubdomain());
        return mapper.toResponse(command.save(para));
    }

    public ParaResponseDto update(Long id, Para para) {
        Para existing = queryService.findById(id);
        tenantGuard.assertSameTenant(existing.getPourashava().getId());

        if (para.getWord() != null && para.getWord().getId() != null) {
            Word word = wordRepository.findById(para.getWord().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Word", "id", para.getWord().getId()));
            existing.setWord(word);
        }

        validator.validateForUpdate(id, para.getPbrName(),
                existing.getWord() != null ? existing.getWord().getId() : null,
                existing.getSubdomain());

        existing.setPbrName(para.getPbrName());
        existing.setCreatedBy(para.getCreatedBy());
        return mapper.toResponse(command.save(existing));
    }

    public void delete(Long id) {
        Para para = queryService.findById(id);
        tenantGuard.assertSameTenant(para.getPourashava().getId());
        command.delete(para);
    }
}
