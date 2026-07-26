package com.insurance.agent.customer;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.customer.dto.CustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/customers") @RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;
    @GetMapping ResponseEntity<?> list(@PageableDefault(size=20, sort="createdAt", direction=Sort.Direction.DESC) Pageable p,
                                       @RequestParam(required=false) String search) { return ResponseEntity.ok(ApiResponse.ok(service.list(p, search))); }
    @GetMapping("/{id}") ResponseEntity<?> get(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.ok(service.get(id))); }
    @GetMapping("/{id}/summary") ResponseEntity<?> summary(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.ok(service.summary(id))); }
    @PostMapping ResponseEntity<?> create(@Valid @RequestBody CustomerRequest r) { return ResponseEntity.status(201).body(ApiResponse.ok(service.create(r), "Customer created")); }
    @PutMapping("/{id}") ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest r) { return ResponseEntity.ok(ApiResponse.ok(service.update(id,r), "Customer updated")); }
    @DeleteMapping("/{id}") ResponseEntity<?> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.ok(ApiResponse.ok(null, "Customer archived")); }
    @PostMapping("/{id}/documents") ResponseEntity<?> upload(@PathVariable UUID id,@RequestParam String docType,@RequestParam MultipartFile file){return ResponseEntity.ok(ApiResponse.ok(service.uploadDocument(id,docType,file),"Document uploaded"));}
}
