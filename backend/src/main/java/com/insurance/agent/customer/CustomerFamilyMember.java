package com.insurance.agent.customer;

import com.insurance.agent.common.enums.FamilyRelation;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "customer_family_members")
public class CustomerFamilyMember {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @Column(nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private FamilyRelation relation;
    private LocalDate dob;
    @Column(name = "is_deleted") private boolean deleted;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
}

