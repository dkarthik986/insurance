package com.insurance.agent.customer;

import com.insurance.agent.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false) private String name;
    private LocalDate dob;
    @Column(nullable = false) private String phone;
    @Column(name = "alternate_phone") private String alternatePhone;
    private String email;
    private String address;
    private String pincode;
    private String city;
    private String state;
    @Column(name = "aadhar_doc_url") private String aadharDocUrl;
    @Column(name = "pan_doc_url") private String panDocUrl;
    @Column(name = "photo_url") private String photoUrl;
    private String notes;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "is_deleted") @Builder.Default private boolean deleted = false;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<CustomerFamilyMember> familyMembers = new ArrayList<>();
    @PreUpdate void touch() { updatedAt = LocalDateTime.now(); }
}

