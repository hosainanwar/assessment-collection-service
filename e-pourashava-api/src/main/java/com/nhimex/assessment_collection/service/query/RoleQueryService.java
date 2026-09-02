package com.nhimex.assessment_collection.service.query;

import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleQueryService {

    private final RoleRepository roleRepository;

    public List<Role> findAllActive() {
        return roleRepository.findWithPermissionsByStatusTrue();
    }

    public Role findById(Long id) {
        return roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }
}
