package com.insurance.agent.policy;

import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.Customer;
import com.insurance.agent.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "policies")
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "policy_number", nullable = false, unique = true) private String policyNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vehicle_id") private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_policy_id") private Policy parentPolicy;
    @Enumerated(EnumType.STRING) @Column(name = "policy_type", nullable = false) private PolicyType policyType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private InsuranceCompany company;
    @Column(name = "plan_name", nullable = false) private String planName;
    @Column(name = "sum_insured") private BigDecimal sumInsured;
    private BigDecimal idv;
    @Column(name = "premium_amount", nullable = false) private BigDecimal premiumAmount;
    @Column(name = "gst_amount") private BigDecimal gstAmount;
    @Column(name = "total_premium") private BigDecimal totalPremium;
    @Enumerated(EnumType.STRING) @Column(name = "payment_frequency") private PaymentFrequency paymentFrequency;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(name = "maturity_date") private LocalDate maturityDate;
    @Column(name = "policy_term_years") private Integer policyTermYears;
    @Column(name = "premium_paying_term") private Integer premiumPayingTerm;
    @Enumerated(EnumType.STRING) @Builder.Default private PolicyStatus status = PolicyStatus.ACTIVE;
    @Column(name = "policy_doc_url") private String policyDocUrl;
    @Column(name = "commission_rate") private BigDecimal commissionRate;
    @Column(name = "commission_amount") private BigDecimal commissionAmount;
    @Column(name = "commission_received") private boolean commissionReceived;
    @Column(name = "commission_received_date") private LocalDate commissionReceivedDate;
    @Column(name = "ncb_percentage") private Integer ncbPercentage;
    @Column(name = "zero_dep") private boolean zeroDep;
    @Column(name = "engine_protect") private boolean engineProtect;
    @Column(name = "ncb_protect") private boolean ncbProtect;
    @Column(name = "roadside_assistance") private boolean roadsideAssistance;
    @Column(name = "long_term") private boolean longTerm;
    private String riders;
    @Column(name = "family_floater") private boolean familyFloater;
    @Column(name = "members_covered") private String membersCovered;
    @Column(name = "pre_existing_disease") private boolean preExistingDisease;
    @Column(name = "waiting_period_days") private Integer waitingPeriodDays;
    private String notes;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "is_deleted") private boolean deleted;
}

