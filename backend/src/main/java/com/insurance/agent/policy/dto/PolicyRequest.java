package com.insurance.agent.policy.dto;
import com.insurance.agent.common.enums.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
public record PolicyRequest(
    @NotBlank String policyNumber,
    @NotNull UUID customerId,
    UUID vehicleId,
    @NotNull PolicyType policyType,
    @NotNull InsuranceCompany company,
    @NotBlank String planName,
    BigDecimal sumInsured,
    BigDecimal idv,
    @NotNull @Positive BigDecimal premiumAmount,
    BigDecimal gstAmount,
    BigDecimal totalPremium,
    PaymentFrequency paymentFrequency,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    LocalDate maturityDate,
    Integer policyTermYears,
    Integer premiumPayingTerm,
    BigDecimal commissionRate,
    String notes
) {}

