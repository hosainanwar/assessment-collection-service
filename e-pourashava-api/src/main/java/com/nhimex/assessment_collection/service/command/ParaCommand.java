package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.repository.ParaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParaCommand {

    private final ParaRepository paraRepository;

    @Transactional
    public Para save(Para para) {
        return paraRepository.save(para);
    }

    @Transactional
    public void delete(Para para) {
        paraRepository.delete(para);
    }
}
