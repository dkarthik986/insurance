package com.insurance.agent.auth.dto;
import com.insurance.agent.common.enums.UserRole;
import java.util.UUID;
public record AuthResponse(String accessToken, String refreshToken, UserRole role, UUID userId, String name, String email) {}

