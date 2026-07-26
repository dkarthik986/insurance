package com.insurance.agent.customer;
import com.insurance.agent.common.enums.*;
import com.insurance.agent.customer.dto.CustomerRequest;
import com.insurance.agent.policy.PolicyRepository;
import com.insurance.agent.document.CloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;
@Service @RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customers;
    private final PolicyRepository policies;
    private final CloudinaryService cloudinary;
    public Page<Map<String,Object>> list(Pageable pageable, String search) {
        var page = search == null || search.isBlank() ? customers.findAllByDeletedFalse(pageable) : customers.search(search, pageable);
        return page.map(this::view);
    }
    public Map<String,Object> get(UUID id) { return view(entity(id)); }
    public Customer entity(UUID id) { return customers.findByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException("Customer not found")); }
    @Transactional public Map<String,Object> create(CustomerRequest r) {
        var c = Customer.builder().name(r.name()).dob(r.dob()).phone(r.phone()).alternatePhone(r.alternatePhone())
            .email(r.email()).address(r.address()).pincode(r.pincode()).city(r.city()).state(r.state()).notes(r.notes()).build();
        return view(customers.save(c));
    }
    @Transactional public Map<String,Object> update(UUID id, CustomerRequest r) {
        var c = entity(id); c.setName(r.name()); c.setDob(r.dob()); c.setPhone(r.phone()); c.setAlternatePhone(r.alternatePhone());
        c.setEmail(r.email()); c.setAddress(r.address()); c.setPincode(r.pincode()); c.setCity(r.city()); c.setState(r.state()); c.setNotes(r.notes());
        return view(customers.save(c));
    }
    @Transactional public void delete(UUID id) {
        var c = entity(id); c.setDeleted(true); customers.save(c);
        policies.findByCustomerIdAndDeletedFalse(id).forEach(p -> { p.setDeleted(true); policies.save(p); });
    }
    public Map<String,Object> summary(UUID id) {
        var c = entity(id); var ps = policies.findByCustomerIdAndDeletedFalse(id);
        var active = ps.stream().filter(p -> p.getStatus() == PolicyStatus.ACTIVE).toList();
        BigDecimal premium = active.stream().map(p -> Optional.ofNullable(p.getTotalPremium()).orElse(p.getPremiumAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal insured = active.stream().map(p -> Optional.ofNullable(p.getSumInsured()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("id", c.getId(), "name", c.getName(), "phone", c.getPhone(), "email", Objects.toString(c.getEmail(),""),
            "activeCount", active.size(), "totalAnnualPremium", premium, "totalSumInsured", insured,
            "hasHealthInsurance", active.stream().anyMatch(p -> p.getPolicyType()==PolicyType.HEALTH),
            "hasLifeInsurance", active.stream().anyMatch(p -> p.getPolicyType()==PolicyType.LIFE),
            "hasVehicleInsurance", active.stream().anyMatch(p -> p.getPolicyType()==PolicyType.VEHICLE));
    }
    @Transactional public String uploadDocument(UUID id, String docType, MultipartFile file) {
        var c = entity(id); var url = cloudinary.upload(file, "insuredesk/customers/" + id);
        if (url == null) url = "local://" + file.getOriginalFilename();
        switch (docType.toUpperCase()) {
            case "AADHAR" -> c.setAadharDocUrl(url);
            case "PAN" -> c.setPanDocUrl(url);
            case "PHOTO" -> c.setPhotoUrl(url);
            default -> throw new IllegalArgumentException("Unsupported document type");
        }
        customers.save(c); return url;
    }
    private Map<String,Object> view(Customer c) {
        var map = new LinkedHashMap<String,Object>(); map.put("id", c.getId()); map.put("name", c.getName()); map.put("dob", c.getDob());
        map.put("phone", c.getPhone()); map.put("alternatePhone", c.getAlternatePhone()); map.put("email", c.getEmail()); map.put("address", c.getAddress());
        map.put("city", c.getCity()); map.put("state", c.getState()); map.put("pincode", c.getPincode()); map.put("notes", c.getNotes());
        map.put("policyCount", policies.findByCustomerIdAndDeletedFalse(c.getId()).size()); map.put("createdAt", c.getCreatedAt()); return map;
    }
}
