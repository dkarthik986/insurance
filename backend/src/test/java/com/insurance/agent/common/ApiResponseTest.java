package com.insurance.agent.common;

import com.insurance.agent.common.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {
    @Test void okEnvelopeContainsTimestamp() {
        var response = ApiResponse.ok("ready");
        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("ready");
        assertThat(response.timestamp()).isNotBlank();
    }
}

