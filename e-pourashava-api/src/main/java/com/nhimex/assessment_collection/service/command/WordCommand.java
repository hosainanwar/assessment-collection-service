package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Word;
import com.nhimex.assessment_collection.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordCommand {

    private final WordRepository wordRepository;

    @Transactional
    public Word save(Word word) {
        return wordRepository.save(word);
    }

    @Transactional
    public void delete(Word word) {
        wordRepository.delete(word);
    }
}
