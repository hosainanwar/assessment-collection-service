package com.nhimex.assessment_collection.dto.response_dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private boolean success;
    private String message;
    private String code;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
