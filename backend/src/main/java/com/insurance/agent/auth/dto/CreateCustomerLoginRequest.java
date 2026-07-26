package com.insurance.agent.auth.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateCustomerLoginRequest(
    @NotNull UUID customerId,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 12, max = 72) String temporaryPassword
) {}

