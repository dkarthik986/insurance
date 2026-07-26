package com.insurance.agent.policy;

import com.insurance.agent.common.enums.*;
import com.insurance.agent.policy.dto.PolicyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PolicyController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class PolicyControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean PolicyService service;
    @MockBean PremiumScheduleRepository schedules;
    @MockBean com.insurance.agent.document.CloudinaryService cloudinary;
    @MockBean com.insurance.agent.config.JwtService jwtService;
    @MockBean com.insurance.agent.config.UserDetailsServiceImpl userDetailsService;

    @Test
    void createsPolicyAndReturnsEnvelope() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any())).thenReturn(Map.of("id", id, "policyNumber", "STH/TEST/001"));
        var request = new PolicyRequest("STH/TEST/001", id, null, PolicyType.HEALTH, InsuranceCompany.STAR_HEALTH,
            "Family Health", null, null, BigDecimal.valueOf(20000), null, null, PaymentFrequency.YEARLY,
            LocalDate.now(), LocalDate.now().plusYears(1), null, null, null, BigDecimal.TEN, "test");

        mvc.perform(post("/api/v1/policies").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.policyNumber").value("STH/TEST/001"));
    }
}
