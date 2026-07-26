package com.insurance.agent.policy;
import com.insurance.agent.common.enums.PremiumInstalment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface PremiumScheduleRepository extends JpaRepository<PremiumSchedule, UUID> {
    List<PremiumSchedule> findByPolicyIdOrderByDueDateAsc(UUID policyId);
    @Query("select s from PremiumSchedule s where s.status='PENDING' and s.dueDate<:today and s.policy.deleted=false")
    List<PremiumSchedule> findOverdue(@Param("today") LocalDate today);
}

