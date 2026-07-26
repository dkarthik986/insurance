package com.insurance.agent.customer.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record CustomerRequest(
    @NotBlank String name,
    LocalDate dob,
    @NotBlank @Pattern(regexp="^\\+?[1-9]\\d{7,14}$") String phone,
    String alternatePhone,
    @Email String email,
    String address,
    String pincode,
    String city,
    String state,
    String notes
) {}

