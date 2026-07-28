package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class DistrictValidatorService {

    public void validateForCreate(String name, String enName, Long divisionId) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserInformException("District name is required");
        }
        if (enName == null || enName.trim().isEmpty()) {
            throw new UserInformException("District English name is required");
        }
        if (divisionId == null) {
            throw new UserInformException("Division ID is required");
        }
    }

    public void validateForUpdate(Long id, String name, String enName, Long divisionId) {
        if (id == null) {
            throw new UserInformException("District ID is required for update");
        }
        validateForCreate(name, enName, divisionId);
    }
}
