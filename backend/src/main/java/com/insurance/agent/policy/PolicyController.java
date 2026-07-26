package com.insurance.agent.policy;
import com.insurance.agent.common.dto.ApiResponse;
import com.insurance.agent.policy.dto.PolicyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.insurance.agent.document.CloudinaryService;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/policies") @RequiredArgsConstructor
public class PolicyController {
    private final PolicyService service; private final PremiumScheduleRepository schedules; private final CloudinaryService cloudinary;
    @GetMapping ResponseEntity<?> list(@PageableDefault(size=20,sort="endDate") Pageable p){return ResponseEntity.ok(ApiResponse.ok(service.list(p)));}
    @GetMapping("/dashboard-stats") ResponseEntity<?> stats(){return ResponseEntity.ok(ApiResponse.ok(service.stats()));}
    @GetMapping("/expiring") ResponseEntity<?> expiring(@RequestParam(defaultValue="15") int days){return ResponseEntity.ok(ApiResponse.ok(service.expiring(days)));}
    @GetMapping("/{id}") ResponseEntity<?> get(@PathVariable UUID id){return ResponseEntity.ok(ApiResponse.ok(service.get(id)));}
    @PostMapping ResponseEntity<?> create(@Valid @RequestBody PolicyRequest r){return ResponseEntity.status(201).body(ApiResponse.ok(service.create(r),"Policy created"));}
    @PutMapping("/{id}") ResponseEntity<?> update(@PathVariable UUID id,@Valid @RequestBody PolicyRequest r){return ResponseEntity.ok(ApiResponse.ok(service.update(id,r),"Policy updated"));}
    @PutMapping("/{id}/renew") ResponseEntity<?> renew(@PathVariable UUID id,@Valid @RequestBody PolicyRequest r){return ResponseEntity.ok(ApiResponse.ok(service.renew(id,r),"Policy renewed"));}
    @DeleteMapping("/{id}") ResponseEntity<?> delete(@PathVariable UUID id){service.delete(id);return ResponseEntity.ok(ApiResponse.ok(null,"Policy archived"));}
    @PostMapping("/{id}/documents") ResponseEntity<?> document(@PathVariable UUID id,@RequestParam MultipartFile file){
        service.entity(id); var url=cloudinary.upload(file,"insuredesk/policies/"+id);
        if(url==null) url="local://"+file.getOriginalFilename(); return ResponseEntity.ok(ApiResponse.ok(service.attachDocument(id,url),"Policy document uploaded"));
    }
    @GetMapping("/{id}/premium-schedule") ResponseEntity<?> schedule(@PathVariable UUID id){return ResponseEntity.ok(ApiResponse.ok(schedules.findByPolicyIdOrderByDueDateAsc(id)));}
    @PutMapping("/premium-schedule/{scheduleId}/mark-paid") ResponseEntity<?> markPaid(@PathVariable UUID scheduleId,@RequestBody java.util.Map<String,String> body){
        var schedule=schedules.findById(scheduleId).orElseThrow(); schedule.setStatus(com.insurance.agent.common.enums.PremiumInstalment.PAID);
        schedule.setPaidDate(body.get("paidDate")==null?java.time.LocalDate.now():java.time.LocalDate.parse(body.get("paidDate"))); schedule.setReceiptNumber(body.get("receiptNumber"));
        return ResponseEntity.ok(ApiResponse.ok(schedules.save(schedule),"Premium marked paid"));
    }
}
