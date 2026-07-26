package com.insurance.agent.notification;
import com.insurance.agent.common.enums.NotificationType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findAll(Pageable pageable);
    long countByReadFalse();
    long countByCustomerIdAndReadFalse(UUID customerId);
    boolean existsByPolicyIdAndType(UUID policyId, NotificationType type);
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}

