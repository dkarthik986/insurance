package com.insurance.agent.policy;
import com.insurance.agent.common.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface PolicyRepository extends JpaRepository<Policy, UUID>, JpaSpecificationExecutor<Policy> {
    Optional<Policy> findByIdAndDeletedFalse(UUID id);
    Page<Policy> findAllByDeletedFalse(Pageable pageable);
    List<Policy> findByCustomerIdAndDeletedFalse(UUID customerId);
    long countByStatusAndDeletedFalse(PolicyStatus status);
    @Query("select p from Policy p where p.deleted=false and p.status='ACTIVE' and p.endDate between :from and :to order by p.endDate")
    List<Policy> expiringBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
    @Query("select p from Policy p where p.deleted=false and p.status='ACTIVE' and p.endDate=:date")
    List<Policy> expiringOn(@Param("date") LocalDate date);
}

