package com.insurance.agent.followup;

import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.Customer;
import com.insurance.agent.policy.Policy;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "follow_ups")
public class FollowUp {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "policy_id") private Policy policy;
    @Column(nullable = false) private String note;
    @Column(name = "follow_up_date", nullable = false) private LocalDate followUpDate;
    @Enumerated(EnumType.STRING) @Builder.Default private FollowUpStatus status = FollowUpStatus.PENDING;
    @Enumerated(EnumType.STRING) @Column(name = "lead_status") @Builder.Default private LeadStatus leadStatus = LeadStatus.WARM;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "is_deleted") private boolean deleted;
}
