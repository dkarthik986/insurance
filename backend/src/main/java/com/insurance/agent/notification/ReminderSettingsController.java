package com.insurance.agent.notification;

import com.insurance.agent.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications/settings")
@RequiredArgsConstructor
public class ReminderSettingsController {
    private final ReminderSettingsRepository repository;
    @GetMapping ResponseEntity<?> get() {
        return ResponseEntity.ok(ApiResponse.ok(repository.findAll().stream().findFirst().orElseGet(() -> repository.save(ReminderSettings.builder().build()))));
    }
    @PutMapping @Transactional ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        var settings = repository.findAll().stream().findFirst().orElseGet(() -> ReminderSettings.builder().build());
        if (body.get("reminderDays") instanceof java.util.List<?> days) settings.setReminderDays(days.stream().map(x -> Integer.valueOf(String.valueOf(x))).toArray(Integer[]::new));
        if (body.containsKey("emailEnabled")) settings.setEmailEnabled(Boolean.parseBoolean(String.valueOf(body.get("emailEnabled"))));
        if (body.containsKey("whatsappEnabled")) settings.setWhatsappEnabled(Boolean.parseBoolean(String.valueOf(body.get("whatsappEnabled"))));
        if (body.containsKey("inAppEnabled")) settings.setInAppEnabled(Boolean.parseBoolean(String.valueOf(body.get("inAppEnabled"))));
        settings.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ok(repository.save(settings), "Reminder settings updated"));
    }
}

