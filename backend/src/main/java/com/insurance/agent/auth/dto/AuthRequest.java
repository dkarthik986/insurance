package com.insurance.agent.auth.dto;
import jakarta.validation.constraints.*;
public record AuthRequest(@NotBlank @Email String email, @NotBlank String password) {}

