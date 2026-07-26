package com.insurance.agent.notification;
import com.insurance.agent.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;
    @GetMapping ResponseEntity<?> list(@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable p){return ResponseEntity.ok(ApiResponse.ok(service.list(p)));}
    @GetMapping("/unread-count") ResponseEntity<?> unread(){return ResponseEntity.ok(ApiResponse.ok(service.unread()));}
    @PostMapping("/send/{id}") ResponseEntity<?> send(@PathVariable UUID id){service.send(id);return ResponseEntity.ok(ApiResponse.ok(null,"Notification sent"));}
    @PutMapping("/{id}/read") ResponseEntity<?> read(@PathVariable UUID id){service.markRead(id);return ResponseEntity.ok(ApiResponse.ok(null,"Marked as read"));}
}
