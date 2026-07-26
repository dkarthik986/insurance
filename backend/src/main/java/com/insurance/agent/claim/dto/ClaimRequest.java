package com.insurance.agent.claim.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClaimRequest(
    @NotNull UUID policyId,
    @NotNull LocalDate claimDate,
    String claimType,
    BigDecimal claimAmount,
    String notes
) {}

