package com.insurance.agent.vehicle;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/v1/vehicles") @RequiredArgsConstructor
public class VehicleController {
    private final VehicleRepository vehicles; private final CustomerRepository customers;
    @GetMapping ResponseEntity<?> list(@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable p){return ResponseEntity.ok(ApiResponse.ok(vehicles.findAllByDeletedFalse(p).map(this::view)));}
    @GetMapping("/{id}") ResponseEntity<?> get(@PathVariable UUID id){return ResponseEntity.ok(ApiResponse.ok(view(entity(id))));}
    @PostMapping @Transactional ResponseEntity<?> create(@RequestBody Map<String,String> b){
        var customer=customers.findByIdAndDeletedFalse(UUID.fromString(b.get("customerId"))).orElseThrow(()->new EntityNotFoundException("Customer not found"));
        var v=Vehicle.builder().customer(customer).regNumber(b.get("regNumber").toUpperCase()).make(b.get("make")).model(b.get("model"))
            .year(Integer.valueOf(b.get("year"))).fuelType(FuelType.valueOf(b.get("fuelType"))).vehicleType(VehicleType.valueOf(b.get("vehicleType")))
            .pucExpiryDate(b.get("pucExpiryDate")==null?null:LocalDate.parse(b.get("pucExpiryDate"))).build();
        return ResponseEntity.status(201).body(ApiResponse.ok(view(vehicles.save(v)),"Vehicle created"));
    }
    @DeleteMapping("/{id}") @Transactional ResponseEntity<?> delete(@PathVariable UUID id){var v=entity(id);v.setDeleted(true);vehicles.save(v);return ResponseEntity.ok(ApiResponse.ok(null,"Vehicle archived"));}
    private Vehicle entity(UUID id){return vehicles.findByIdAndDeletedFalse(id).orElseThrow(()->new EntityNotFoundException("Vehicle not found"));}
    private Map<String,Object> view(Vehicle v){return Map.of("id",v.getId(),"customerId",v.getCustomer().getId(),"owner",v.getCustomer().getName(),"regNumber",v.getRegNumber(),"make",Objects.toString(v.getMake(),""),"model",Objects.toString(v.getModel(),""),"year",Objects.requireNonNullElse(v.getYear(),0),"fuelType",Objects.toString(v.getFuelType(),""),"vehicleType",v.getVehicleType(),"pucExpiryDate",Objects.toString(v.getPucExpiryDate(),""));}
}
