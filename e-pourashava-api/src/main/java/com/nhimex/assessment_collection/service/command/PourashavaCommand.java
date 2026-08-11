package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.repository.PourashavaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PourashavaCommand {

    private final PourashavaRepository pourashavaRepository;

    @Transactional
    public Pourashava save(Pourashava pourashava) {
        return pourashavaRepository.save(pourashava);
    }

    @Transactional
    public void delete(Pourashava pourashava) {
        pourashavaRepository.delete(pourashava);
    }
}
