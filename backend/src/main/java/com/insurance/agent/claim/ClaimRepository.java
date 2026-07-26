package com.insurance.agent.claim;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Page<Claim> findAllByDeletedFalse(Pageable pageable);
    List<Claim> findByPolicyIdAndDeletedFalse(UUID policyId);
}

