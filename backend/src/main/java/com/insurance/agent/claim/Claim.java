package com.insurance.agent.claim;

import com.insurance.agent.common.enums.ClaimStatus;
import com.insurance.agent.policy.Policy;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "claims")
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "policy_id", nullable = false) private Policy policy;
    @Column(name = "claim_number") private String claimNumber;
    @Column(name = "claim_date", nullable = false) private LocalDate claimDate;
    @Column(name = "claim_type") private String claimType;
    @Column(name = "claim_amount") private BigDecimal claimAmount;
    @Enumerated(EnumType.STRING) @Builder.Default private ClaimStatus status = ClaimStatus.FILED;
    @Column(name = "settled_amount") private BigDecimal settledAmount;
    @Column(name = "settlement_date") private LocalDate settlementDate;
    @Column(name = "doc_url") private String docUrl;
    private String notes;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "is_deleted") private boolean deleted;
}

