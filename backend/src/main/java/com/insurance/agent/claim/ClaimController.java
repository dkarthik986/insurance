package com.insurance.agent.claim;

import com.insurance.agent.claim.dto.ClaimRequest;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.common.enums.ClaimStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService service;

    @GetMapping
    ResponseEntity<?> list(
        @PageableDefault(size = 20, sort = "claimDate", direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(required = false) UUID policyId,
        @RequestParam(required = false) ClaimStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(pageable, policyId, status)));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.get(id)));
    }

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody ClaimRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.create(request), "Claim filed"));
    }

    @PutMapping("/{id}/status")
    ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        var settledAmount = body.get("settledAmount") == null ? null : new java.math.BigDecimal(body.get("settledAmount"));
        var settlementDate = body.get("settlementDate") == null ? null : LocalDate.parse(body.get("settlementDate"));
        return ResponseEntity.ok(ApiResponse.ok(service.updateStatus(id, ClaimStatus.valueOf(body.get("status")), settledAmount, settlementDate), "Claim status updated"));
    }
}

