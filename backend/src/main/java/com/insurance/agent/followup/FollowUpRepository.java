package com.insurance.agent.followup;
import com.insurance.agent.common.enums.FollowUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {
    List<FollowUp> findByFollowUpDateAndStatusAndDeletedFalse(LocalDate date, FollowUpStatus status);
    List<FollowUp> findByCustomerIdAndDeletedFalseOrderByFollowUpDateDesc(UUID customerId);
}

