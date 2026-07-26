package com.insurance.agent.vehicle;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByIdAndDeletedFalse(UUID id);
    Optional<Vehicle> findByRegNumberIgnoreCaseAndDeletedFalse(String regNumber);
    Page<Vehicle> findAllByDeletedFalse(Pageable pageable);
    List<Vehicle> findByCustomerIdAndDeletedFalse(UUID customerId);
}

