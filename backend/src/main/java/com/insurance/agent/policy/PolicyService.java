package com.insurance.agent.policy;
import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.CustomerRepository;
import com.insurance.agent.notification.NotificationService;
import com.insurance.agent.policy.dto.PolicyRequest;
import com.insurance.agent.vehicle.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;
@Service @RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policies; private final CustomerRepository customers; private final VehicleRepository vehicles;
    private final PremiumScheduleRepository schedules; private final NotificationService notifications;
    public Page<Map<String,Object>> list(Pageable p) { return policies.findAllByDeletedFalse(p).map(this::view); }
    public Map<String,Object> get(UUID id) { return view(entity(id)); }
    public Policy entity(UUID id) { return policies.findByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("Policy not found")); }
    @Transactional public Map<String,Object> create(PolicyRequest r) {
        var customer = customers.findByIdAndDeletedFalse(r.customerId()).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        var policy = Policy.builder().policyNumber(r.policyNumber()).customer(customer).policyType(r.policyType()).company(r.company())
            .planName(r.planName()).sumInsured(r.sumInsured()).idv(r.idv()).premiumAmount(r.premiumAmount()).gstAmount(r.gstAmount())
            .totalPremium(r.totalPremium()==null?r.premiumAmount():r.totalPremium()).paymentFrequency(r.paymentFrequency()==null?PaymentFrequency.YEARLY:r.paymentFrequency())
            .startDate(r.startDate()).endDate(r.endDate()).maturityDate(r.maturityDate()).policyTermYears(r.policyTermYears())
            .premiumPayingTerm(r.premiumPayingTerm()).commissionRate(r.commissionRate()).commissionAmount(commission(r.premiumAmount(),r.commissionRate()))
            .notes(r.notes()).build();
        if (r.vehicleId()!=null) policy.setVehicle(vehicles.findByIdAndDeletedFalse(r.vehicleId()).orElseThrow(() -> new EntityNotFoundException("Vehicle not found")));
        policy = policies.save(policy);
        if (policy.getCompany()==InsuranceCompany.LIC) generateSchedule(policy);
        notifications.policyAdded(policy);
        return view(policy);
    }
    @Transactional public Map<String,Object> renew(UUID id, PolicyRequest r) {
        var old = entity(id); old.setStatus(PolicyStatus.RENEWED); policies.save(old);
        var customer = old.getCustomer();
        var renewed = Policy.builder().policyNumber(r.policyNumber()).customer(customer).vehicle(old.getVehicle()).parentPolicy(old)
            .policyType(old.getPolicyType()).company(old.getCompany()).planName(r.planName()).sumInsured(r.sumInsured())
            .premiumAmount(r.premiumAmount()).totalPremium(r.totalPremium()==null?r.premiumAmount():r.totalPremium())
            .paymentFrequency(r.paymentFrequency()).startDate(r.startDate()).endDate(r.endDate()).commissionRate(r.commissionRate())
            .commissionAmount(commission(r.premiumAmount(),r.commissionRate())).build();
        renewed=policies.save(renewed); notifications.renewalDone(renewed); return view(renewed);
    }
    @Transactional public Map<String,Object> update(UUID id, PolicyRequest r) {
        var p = entity(id);
        var customer = customers.findByIdAndDeletedFalse(r.customerId()).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        p.setPolicyNumber(r.policyNumber()); p.setCustomer(customer); p.setPolicyType(r.policyType()); p.setCompany(r.company());
        p.setPlanName(r.planName()); p.setSumInsured(r.sumInsured()); p.setIdv(r.idv()); p.setPremiumAmount(r.premiumAmount());
        p.setGstAmount(r.gstAmount()); p.setTotalPremium(r.totalPremium() == null ? r.premiumAmount() : r.totalPremium());
        p.setPaymentFrequency(r.paymentFrequency() == null ? PaymentFrequency.YEARLY : r.paymentFrequency());
        p.setStartDate(r.startDate()); p.setEndDate(r.endDate()); p.setMaturityDate(r.maturityDate());
        p.setPolicyTermYears(r.policyTermYears()); p.setPremiumPayingTerm(r.premiumPayingTerm());
        p.setCommissionRate(r.commissionRate()); p.setCommissionAmount(commission(r.premiumAmount(), r.commissionRate())); p.setNotes(r.notes());
        return view(policies.save(p));
    }
    @Transactional public void delete(UUID id) { var p=entity(id); p.setDeleted(true); policies.save(p); }
    @Transactional public String attachDocument(UUID id, String url) { var p=entity(id); p.setPolicyDocUrl(url); policies.save(p); return url; }
    public List<Map<String,Object>> expiring(int days) { return policies.expiringBetween(LocalDate.now(), LocalDate.now().plusDays(days)).stream().map(this::view).toList(); }
    public Map<String,Object> stats() {
        var exp15=policies.expiringBetween(LocalDate.now(),LocalDate.now().plusDays(15));
        return Map.of("totalActive",policies.countByStatusAndDeletedFalse(PolicyStatus.ACTIVE),
            "totalExpired",policies.countByStatusAndDeletedFalse(PolicyStatus.EXPIRED),"expiringIn15Days",exp15.size(),
            "expiringIn30Days",policies.expiringBetween(LocalDate.now(),LocalDate.now().plusDays(30)).size(),
            "totalCommissionThisMonth",exp15.stream().map(p->Optional.ofNullable(p.getCommissionAmount()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add),
            "newCustomersThisMonth",0,"overduePremiums",schedules.findOverdue(LocalDate.now()).size(),"pendingFollowUps",0,"todayBirthdays",0);
    }
    private BigDecimal commission(BigDecimal premium, BigDecimal rate) { return rate==null?BigDecimal.ZERO:premium.multiply(rate).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP); }
    private void generateSchedule(Policy p) {
        int months=switch(p.getPaymentFrequency()){case MONTHLY->1; case QUARTERLY->3; case HALF_YEARLY->6; default->12;};
        LocalDate due=p.getStartDate(); LocalDate until=p.getStartDate().plusYears(Optional.ofNullable(p.getPremiumPayingTerm()).orElse(1));
        while(due.isBefore(until)){ schedules.save(PremiumSchedule.builder().policy(p).dueDate(due).amount(p.getPremiumAmount()).build()); due=due.plusMonths(months); }
    }
    public Map<String,Object> view(Policy p) {
        var m=new LinkedHashMap<String,Object>(); m.put("id",p.getId());m.put("policyNumber",p.getPolicyNumber());m.put("customerId",p.getCustomer().getId());
        m.put("customerName",p.getCustomer().getName());m.put("customerPhone",p.getCustomer().getPhone());m.put("policyType",p.getPolicyType());m.put("company",p.getCompany());
        m.put("planName",p.getPlanName());m.put("sumInsured",p.getSumInsured());m.put("premiumAmount",p.getPremiumAmount());m.put("totalPremium",p.getTotalPremium());
        m.put("startDate",p.getStartDate());m.put("endDate",p.getEndDate());m.put("status",p.getStatus());m.put("commissionAmount",p.getCommissionAmount());
        m.put("daysUntilExpiry",ChronoUnit.DAYS.between(LocalDate.now(),p.getEndDate()));m.put("vehicleRegNumber",p.getVehicle()==null?null:p.getVehicle().getRegNumber());return m;
    }
}
