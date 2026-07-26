package com.insurance.agent.notification;

import com.insurance.agent.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsAppService {
    private final RestTemplate restTemplate;
    @Value("${app.whatsapp.api-url:https://graph.facebook.com/v18.0}") private String apiUrl;
    @Value("${app.whatsapp.token:}") private String token;
    @Value("${app.whatsapp.phone-number-id:}") private String phoneNumberId;

    public void sendExpiryReminder(Policy policy, int daysLeft) {
        if (token.isBlank() || phoneNumberId.isBlank() || policy.getCustomer().getPhone() == null) return;
        var payload = Map.of(
            "messaging_product", "whatsapp",
            "to", policy.getCustomer().getPhone().replaceAll("\\D", ""),
            "type", "template",
            "template", Map.of("name", "policy_expiry_reminder", "language", Map.of("code", "en"),
                "components", java.util.List.of(Map.of("type", "body", "parameters", java.util.List.of(
                    Map.of("type", "text", "text", policy.getCustomer().getName()),
                    Map.of("type", "text", "text", policy.getPlanName()),
                    Map.of("type", "text", "text", policy.getPolicyNumber()),
                    Map.of("type", "text", "text", String.valueOf(daysLeft))
                ))))
        );
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(apiUrl + "/" + phoneNumberId + "/messages", new HttpEntity<>(payload, headers), String.class);
    }
}

