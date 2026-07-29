package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Para;
import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.ParaRepository;
import com.nhimex.assessment_collection.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParaCommand {

    private final ParaRepository paraRepository;
    private final WordRepository wordRepository;

    @Transactional
    public Para create(Para para) {
        if (para.getWord() == null || para.getWord().getId() == null) {
            throw new ResourceNotFoundException("Word", "id", null);
        }
        Word word = wordRepository.findById(para.getWord().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Word", "id", para.getWord().getId()));
        para.setWord(word);
        return paraRepository.save(para);
    }

    @Transactional
    public Para update(Long id, Para para) {
        Para existing = paraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Para", "id", id));

        if (para.getWord() != null && para.getWord().getId() != null) {
            Word word = wordRepository.findById(para.getWord().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Word", "id", para.getWord().getId()));
            existing.setWord(word);
        }

        existing.setPbrName(para.getPbrName());
        existing.setSubdomain(para.getSubdomain());
        existing.setCreatedBy(para.getCreatedBy());

        return paraRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Para para = paraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Para", "id", id));
        paraRepository.delete(para);
    }
}
