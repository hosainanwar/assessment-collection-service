package com.nhimex.assessment_collection.security;

import com.nhimex.assessment_collection.entity.Pourashava;
import com.nhimex.assessment_collection.exception.ResourceNotFoundException;
import com.nhimex.assessment_collection.repository.PourashavaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TenantGuard {

    private final PourashavaRepository pourashavaRepository;

    public void assertSameTenant(Long resourcePourashavaId) {
        UserPrincipal principal = CurrentUserProvider.require();
        if (principal.isSuperAdmin()) {
            return;
        }
        if (resourcePourashavaId == null || !principal.getPourashavaId().equals(resourcePourashavaId)) {
            throw new AccessDeniedException("Cannot access another pourashava's data");
        }
    }

    public Pourashava requireCurrentPourashava() {
        UserPrincipal principal = CurrentUserProvider.require();
        return pourashavaRepository.findById(principal.getPourashavaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "id", principal.getPourashavaId()));
    }

    /**
     * Super admin may pick a pourashava by id or subdomain; everyone else is forced to their own.
     */
    public Pourashava resolvePourashava(Long requestedId, String subdomain) {
        UserPrincipal principal = CurrentUserProvider.require();
        if (!principal.isSuperAdmin()) {
            return requireCurrentPourashava();
        }
        if (requestedId != null) {
            return pourashavaRepository.findById(requestedId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "id", requestedId));
        }
        if (StringUtils.hasText(subdomain)) {
            return pourashavaRepository.findBySubdomain(subdomain.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Pourashava", "subdomain", subdomain));
        }
        return requireCurrentPourashava();
    }

    public boolean isDemo(Pourashava pourashava) {
        return pourashava != null && RoleCodes.DEMO_SUBDOMAIN.equalsIgnoreCase(pourashava.getSubdomain());
    }
}
