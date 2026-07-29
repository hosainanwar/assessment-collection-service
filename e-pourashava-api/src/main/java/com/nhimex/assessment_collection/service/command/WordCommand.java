package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordCommand {

    private final WordRepository wordRepository;

    @Transactional
    public Word create(Word word) {
        return wordRepository.save(word);
    }

    @Transactional
    public Word update(Long id, Word word) {
        Word existing = wordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Word", "id", id));

        existing.setWordName(word.getWordName());
        existing.setSubdomain(word.getSubdomain());
        existing.setCreatedBy(word.getCreatedBy());

        return wordRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Word", "id", id));
        wordRepository.delete(word);
    }
}
