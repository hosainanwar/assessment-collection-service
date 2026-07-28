package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Division;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DivisionCommand {

    private final DivisionRepository divisionRepository;

    @Transactional
    public Division create(Division division) {
        if (divisionRepository.existsByName(division.getName())) {
            throw new UserInformException("Division name already exists: " + division.getName());
        }
        return divisionRepository.save(division);
    }

    @Transactional
    public Division update(Long id, Division division) {
        Division existing = divisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", id));

        if (!existing.getName().equals(division.getName()) &&
                divisionRepository.existsByName(division.getName())) {
            throw new UserInformException("Division name already exists: " + division.getName());
        }

        existing.setName(division.getName());
        return divisionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Division", "id", id));
        divisionRepository.delete(division);
    }
}
