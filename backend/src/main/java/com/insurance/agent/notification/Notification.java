package com.insurance.agent.notification;

import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.Customer;
import com.insurance.agent.policy.Policy;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "policy_id") private Policy policy;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private Customer customer;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private NotificationChannel channel;
    @Column(nullable = false) private String message;
    @Column(name = "is_sent") private boolean sent;
    @Column(name = "is_read") private boolean read;
    @Column(name = "sent_at") private LocalDateTime sentAt;
    @Column(name = "scheduled_for") private LocalDate scheduledFor;
    @Column(name = "error_message") private String errorMessage;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}

