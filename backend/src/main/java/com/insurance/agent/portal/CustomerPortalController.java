package com.insurance.agent.portal;
import com.insurance.agent.auth.User;
import com.insurance.agent.claim.ClaimRepository;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.customer.CustomerRepository;
import com.insurance.agent.followup.*;
import com.insurance.agent.notification.NotificationRepository;
import com.insurance.agent.policy.PolicyRepository;
import com.insurance.agent.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/v1/portal") @RequiredArgsConstructor
public class CustomerPortalController {
    private final CustomerRepository customers; private final PolicyRepository policies; private final VehicleRepository vehicles;
    private final ClaimRepository claims; private final NotificationRepository notifications; private final FollowUpRepository followups;
    @GetMapping("/my-policies") ResponseEntity<?> policies(@AuthenticationPrincipal User user){var c=customer(user);return ResponseEntity.ok(ApiResponse.ok(policies.findByCustomerIdAndDeletedFalse(c.getId())));}
    @GetMapping("/my-vehicles") ResponseEntity<?> vehicles(@AuthenticationPrincipal User user){var c=customer(user);return ResponseEntity.ok(ApiResponse.ok(vehicles.findByCustomerIdAndDeletedFalse(c.getId())));}
    @GetMapping("/my-claims") ResponseEntity<?> claims(@AuthenticationPrincipal User user){var c=customer(user);var all=policies.findByCustomerIdAndDeletedFalse(c.getId()).stream().flatMap(p->claims.findByPolicyIdAndDeletedFalse(p.getId()).stream()).toList();return ResponseEntity.ok(ApiResponse.ok(all));}
    @GetMapping("/my-notifications") ResponseEntity<?> notifications(@AuthenticationPrincipal User user){return ResponseEntity.ok(ApiResponse.ok(notifications.findByCustomerIdOrderByCreatedAtDesc(customer(user).getId())));}
    @PostMapping("/renewal-request") @Transactional ResponseEntity<?> renewal(@AuthenticationPrincipal User user,@RequestBody Map<String,String> b){var c=customer(user);var p=policies.findByIdAndDeletedFalse(UUID.fromString(b.get("policyId"))).orElseThrow();if(!p.getCustomer().getId().equals(c.getId()))throw new org.springframework.security.access.AccessDeniedException("Not your policy");followups.save(FollowUp.builder().customer(c).policy(p).note("Customer requested renewal for policy "+p.getPolicyNumber()).followUpDate(LocalDate.now()).build());return ResponseEntity.ok(ApiResponse.ok(null,"Your renewal request has been sent to your agent"));}
    private com.insurance.agent.customer.Customer customer(User user){return customers.findByUserIdAndDeletedFalse(user.getId()).orElseThrow();}
}
