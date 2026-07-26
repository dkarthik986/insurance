package com.insurance.agent.policy;

import com.insurance.agent.common.enums.PremiumInstalment;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "premium_schedule")
public class PremiumSchedule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "policy_id", nullable = false) private Policy policy;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(nullable = false) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Builder.Default private PremiumInstalment status = PremiumInstalment.PENDING;
    @Column(name = "paid_date") private LocalDate paidDate;
    @Column(name = "receipt_number") private String receiptNumber;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}

