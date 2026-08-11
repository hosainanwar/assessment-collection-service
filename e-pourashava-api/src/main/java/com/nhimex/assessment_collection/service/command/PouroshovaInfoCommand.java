package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.PouroshovaInfo;
import com.nhimex.assessment_collection.repository.PouroshovaInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PouroshovaInfoCommand {

    private final PouroshovaInfoRepository pouroshovaInfoRepository;

    @Transactional
    public PouroshovaInfo save(PouroshovaInfo pouroshovaInfo) {
        return pouroshovaInfoRepository.save(pouroshovaInfo);
    }

    @Transactional
    public void delete(PouroshovaInfo pouroshovaInfo) {
        pouroshovaInfoRepository.delete(pouroshovaInfo);
    }
}
