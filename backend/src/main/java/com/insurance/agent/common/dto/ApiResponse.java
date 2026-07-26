package com.insurance.agent.common.dto;

import java.time.OffsetDateTime;

public record ApiResponse<T>(boolean success, T data, String message, String timestamp) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, OffsetDateTime.now().toString());
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, OffsetDateTime.now().toString());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, OffsetDateTime.now().toString());
    }
}

