package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.exception.UserInformException;
import org.springframework.stereotype.Service;

@Service
public class PourashavaValidatorService {

    public void validateForCreate(String bnName, String enName, String subdomain, Long divisionId, Long districtId) {
        if (bnName == null || bnName.trim().isEmpty()) {
            throw new UserInformException("Bangla name is required");
        }
        if (enName == null || enName.trim().isEmpty()) {
            throw new UserInformException("English name is required");
        }
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new UserInformException("Subdomain is required");
        }
        if (divisionId == null) {
            throw new UserInformException("Division ID is required");
        }
        if (districtId == null) {
            throw new UserInformException("District ID is required");
        }
    }

    public void validateForUpdate(Long id, String bnName, String enName, String subdomain, Long divisionId, Long districtId) {
        if (id == null) {
            throw new UserInformException("Pourashava ID is required for update");
        }
        validateForCreate(bnName, enName, subdomain, divisionId, districtId);
    }
}
