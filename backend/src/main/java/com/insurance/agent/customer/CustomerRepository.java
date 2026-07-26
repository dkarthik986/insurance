package com.insurance.agent.customer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByIdAndDeletedFalse(UUID id);
    Optional<Customer> findByUserIdAndDeletedFalse(UUID userId);
    Page<Customer> findAllByDeletedFalse(Pageable pageable);
    @Query("select c from Customer c where c.deleted=false and " +
           "(lower(c.name) like lower(concat('%',:q,'%')) or c.phone like concat('%',:q,'%') or lower(c.email) like lower(concat('%',:q,'%')))")
    Page<Customer> search(@Param("q") String query, Pageable pageable);
    long countByDeletedFalse();
}
