package com.nhimex.assessment_collection.service.validator;

import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.exception.UserInformException;
import com.nhimex.assessment_collection.repository.RoleRepository;
import com.nhimex.assessment_collection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RoleValidatorService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public void validateForCreate(String code) {
        if (!StringUtils.hasText(code)) {
            throw new UserInformException("Role code is required");
        }
        String normalized = code.trim().toUpperCase();
        if (!normalized.matches("[A-Z][A-Z0-9_]{1,49}")) {
            throw new UserInformException("Role code must be uppercase letters, numbers, and underscores");
        }
        if (roleRepository.existsByCode(normalized)) {
            throw new UserInformException("Role code already exists: " + normalized);
        }
    }

    public void validateForUpdate(Role existing, String newCode) {
        if (Boolean.TRUE.equals(existing.getIsSystem()) && newCode != null
                && !existing.getCode().equalsIgnoreCase(newCode.trim())) {
            throw new UserInformException("System roles cannot be renamed");
        }
        if (StringUtils.hasText(newCode) && !existing.getCode().equalsIgnoreCase(newCode.trim())
                && roleRepository.existsByCode(newCode.trim().toUpperCase())) {
            throw new UserInformException("Role code already exists: " + newCode);
        }
    }

    public void validateForDelete(Role existing) {
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new UserInformException("System roles cannot be deleted");
        }
        if (userRepository.countByRoles_Id(existing.getId()) > 0) {
            throw new UserInformException("Role is assigned to users; unassign it first");
        }
    }
}
