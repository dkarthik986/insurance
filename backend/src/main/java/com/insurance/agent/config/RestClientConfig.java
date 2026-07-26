package com.insurance.agent.config;

import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
    @Bean RestTemplate restTemplate() { return new RestTemplate(); }
}

