package com.nhimex.assessment_collection.service.command;

import com.nhimex.assessment_collection.entity.Role;
import com.nhimex.assessment_collection.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCommand {

    private final RoleRepository roleRepository;

    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void delete(Role role) {
        roleRepository.delete(role);
    }
}
