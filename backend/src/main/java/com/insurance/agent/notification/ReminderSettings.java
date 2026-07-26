package com.insurance.agent.notification;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "reminder_settings")
public class ReminderSettings {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @JdbcTypeCode(SqlTypes.ARRAY) @Column(name = "reminder_days", columnDefinition = "integer[]")
    @Builder.Default private Integer[] reminderDays = new Integer[]{30, 15, 7, 1};
    @Column(name = "email_enabled") @Builder.Default private boolean emailEnabled = true;
    @Column(name = "whatsapp_enabled") @Builder.Default private boolean whatsappEnabled = true;
    @Column(name = "in_app_enabled") @Builder.Default private boolean inAppEnabled = true;
    @Column(name = "updated_at") @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
}

