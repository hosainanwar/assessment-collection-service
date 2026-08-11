package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DivisionCommand {

    private final DivisionRepository divisionRepository;

    @Transactional
    public Division save(Division division) {
        return divisionRepository.save(division);
    }

    @Transactional
    public void delete(Division division) {
        divisionRepository.delete(division);
    }
}
