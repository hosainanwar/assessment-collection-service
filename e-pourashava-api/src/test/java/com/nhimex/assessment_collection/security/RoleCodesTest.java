package com.nhimex.assessment_collection.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleCodesTest {

    @Test
    void fromLegacyMapsAdminAndNull() {
        assertEquals(RoleCodes.SUPER_ADMIN, RoleCodes.fromLegacy("SUPER_ADMIN"));
        assertEquals(RoleCodes.POURASHAVA_ADMIN, RoleCodes.fromLegacy("ADMIN"));
        assertEquals(RoleCodes.POURASHAVA_ADMIN, RoleCodes.fromLegacy("POURASHAVA_ADMIN"));
        assertEquals(RoleCodes.OPERATOR, RoleCodes.fromLegacy("OPERATOR"));
        assertEquals(RoleCodes.VIEWER, RoleCodes.fromLegacy("USER"));
        assertEquals(RoleCodes.VIEWER, RoleCodes.fromLegacy(null));
    }

    @Test
    void principalMatchesTenantBySubdomainOrId() {
        UserPrincipal principal = UserPrincipal.builder()
                .userId(1L)
                .username("admin")
                .pourashavaId(7L)
                .subdomain("sreepur")
                .roleCodes(java.util.List.of(RoleCodes.POURASHAVA_ADMIN))
                .permissionCodes(java.util.List.of())
                .enabled(true)
                .authorities(java.util.List.of())
                .build();

        assertTrue(principal.matchesTenant("sreepur"));
        assertTrue(principal.matchesTenant("7"));
        assertFalse(principal.matchesTenant("gazipur"));
        assertFalse(principal.matchesTenant(null));
        assertFalse(principal.isSuperAdmin());
    }
}
