package com.insurance.agent.auth.dto;
import jakarta.validation.constraints.*;
public record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min=12,max=72) String newPassword) {}
