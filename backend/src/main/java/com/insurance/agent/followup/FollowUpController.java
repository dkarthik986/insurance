package com.insurance.agent.followup;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/v1/followups") @RequiredArgsConstructor
public class FollowUpController {
    private final FollowUpRepository followups; private final CustomerRepository customers;
    @GetMapping("/today") ResponseEntity<?> today(){return ResponseEntity.ok(ApiResponse.ok(followups.findByFollowUpDateAndStatusAndDeletedFalse(LocalDate.now(),FollowUpStatus.PENDING).stream().map(this::view).toList()));}
    @GetMapping("/customer/{id}") ResponseEntity<?> customer(@PathVariable UUID id){return ResponseEntity.ok(ApiResponse.ok(followups.findByCustomerIdAndDeletedFalseOrderByFollowUpDateDesc(id).stream().map(this::view).toList()));}
    @PostMapping @Transactional ResponseEntity<?> create(@RequestBody Map<String,String> b){
        var c=customers.findByIdAndDeletedFalse(UUID.fromString(b.get("customerId"))).orElseThrow(()->new EntityNotFoundException("Customer not found"));
        var f=followups.save(FollowUp.builder().customer(c).note(b.get("note")).followUpDate(LocalDate.parse(b.get("followUpDate"))).leadStatus(LeadStatus.valueOf(b.getOrDefault("leadStatus","WARM"))).build());
        return ResponseEntity.status(201).body(ApiResponse.ok(view(f),"Follow-up created"));
    }
    private Map<String,Object> view(FollowUp f){return Map.of("id",f.getId(),"customerId",f.getCustomer().getId(),"customerName",f.getCustomer().getName(),"customerPhone",f.getCustomer().getPhone(),"note",f.getNote(),"followUpDate",f.getFollowUpDate(),"status",f.getStatus(),"leadStatus",f.getLeadStatus());}
}

