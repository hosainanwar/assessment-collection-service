package com.nhimex.assessment_collection.security;

public final class RoleCodes {

    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String POURASHAVA_ADMIN = "POURASHAVA_ADMIN";
    public static final String OPERATOR = "OPERATOR";
    public static final String VIEWER = "VIEWER";

    public static final String DEMO_SUBDOMAIN = "demo";

    private RoleCodes() {
    }

    public static String fromLegacy(String legacy) {
        if (legacy == null || legacy.isBlank()) {
            return VIEWER;
        }
        return switch (legacy.trim().toUpperCase()) {
            case SUPER_ADMIN -> SUPER_ADMIN;
            case "ADMIN", POURASHAVA_ADMIN -> POURASHAVA_ADMIN;
            case OPERATOR -> OPERATOR;
            default -> VIEWER;
        };
    }
}
