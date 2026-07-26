package com.insurance.agent.claim;

import com.insurance.agent.claim.dto.ClaimRequest;
import com.insurance.agent.common.enums.ClaimStatus;
import com.insurance.agent.policy.PolicyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claims;
    private final PolicyRepository policies;

    public Page<Map<String, Object>> list(Pageable pageable, UUID policyId, ClaimStatus status) {
        Page<Claim> page = policyId == null
            ? claims.findAllByDeletedFalse(pageable)
            : new PageImpl<>(claims.findByPolicyIdAndDeletedFalse(policyId), pageable, claims.findByPolicyIdAndDeletedFalse(policyId).size());
        return page.map(this::view);
    }

    public Map<String, Object> get(UUID id) {
        return view(claims.findById(id).filter(c -> !c.isDeleted())
            .orElseThrow(() -> new EntityNotFoundException("Claim not found")));
    }

    @Transactional
    public Map<String, Object> create(ClaimRequest request) {
        var policy = policies.findByIdAndDeletedFalse(request.policyId())
            .orElseThrow(() -> new EntityNotFoundException("Policy not found"));
        var claim = Claim.builder()
            .policy(policy)
            .claimNumber("CLM-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
            .claimDate(request.claimDate())
            .claimType(request.claimType())
            .claimAmount(request.claimAmount())
            .notes(request.notes())
            .status(ClaimStatus.FILED)
            .build();
        return view(claims.save(claim));
    }

    @Transactional
    public Map<String, Object> updateStatus(UUID id, ClaimStatus status, java.math.BigDecimal settledAmount, LocalDate settlementDate) {
        var claim = claims.findById(id).filter(c -> !c.isDeleted())
            .orElseThrow(() -> new EntityNotFoundException("Claim not found"));
        claim.setStatus(status);
        claim.setSettledAmount(settledAmount);
        claim.setSettlementDate(settlementDate);
        return view(claims.save(claim));
    }

    private Map<String, Object> view(Claim c) {
        var result = new LinkedHashMap<String, Object>();
        result.put("id", c.getId());
        result.put("claimNumber", c.getClaimNumber());
        result.put("policyId", c.getPolicy().getId());
        result.put("policyNumber", c.getPolicy().getPolicyNumber());
        result.put("customerName", c.getPolicy().getCustomer().getName());
        result.put("claimDate", c.getClaimDate());
        result.put("claimType", c.getClaimType());
        result.put("claimAmount", c.getClaimAmount());
        result.put("status", c.getStatus());
        result.put("settledAmount", c.getSettledAmount());
        result.put("settlementDate", c.getSettlementDate());
        result.put("notes", c.getNotes());
        return result;
    }
}

