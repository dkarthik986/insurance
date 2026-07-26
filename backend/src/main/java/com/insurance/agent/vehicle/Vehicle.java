package com.insurance.agent.vehicle;

import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.Customer;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "vehicles")
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @Column(name = "reg_number", nullable = false, unique = true) private String regNumber;
    private String make;
    private String model;
    private Integer year;
    @Enumerated(EnumType.STRING) @Column(name = "fuel_type") private FuelType fuelType;
    @Enumerated(EnumType.STRING) @Column(name = "vehicle_type", nullable = false) private VehicleType vehicleType;
    @Column(name = "chassis_number") private String chassisNumber;
    @Column(name = "engine_number") private String engineNumber;
    @Column(name = "rc_doc_url") private String rcDocUrl;
    @Column(name = "puc_doc_url") private String pucDocUrl;
    @Column(name = "puc_expiry_date") private LocalDate pucExpiryDate;
    @Column(name = "created_at", updatable = false) @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") @Builder.Default private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "is_deleted") private boolean deleted;
}

