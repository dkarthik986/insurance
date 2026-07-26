package com.insurance.agent.notification;
import com.insurance.agent.common.enums.*;
import com.insurance.agent.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    @Async @Transactional public void policyAdded(Policy p) { createAll(p, NotificationType.POLICY_ADDED, "Policy " + p.getPolicyNumber() + " has been added successfully."); }
    @Async @Transactional public void renewalDone(Policy p) { createAll(p, NotificationType.RENEWAL_DONE, "Policy " + p.getPolicyNumber() + " renewal is complete."); }
    @Transactional public void expiryReminder(Policy p, int days) {
        NotificationType type=switch(days){case 30->NotificationType.EXPIRY_30D;case 15->NotificationType.EXPIRY_15D;case 7->NotificationType.EXPIRY_7D;default->NotificationType.EXPIRY_1D;};
        if(!repository.existsByPolicyIdAndType(p.getId(),type)) {
            String message = "Your " + p.getPlanName() + " policy expires in " + days + " day" + (days == 1 ? "" : "s") + ".";
            createAll(p, type, message);
            emailService.sendPolicyMessage(p, "Policy expiry reminder", message);
            if (days <= 15) whatsAppService.sendExpiryReminder(p, days);
        }
    }
    private void createAll(Policy p, NotificationType type, String message) {
        for(var channel:NotificationChannel.values()) repository.save(Notification.builder().policy(p).customer(p.getCustomer()).type(type).channel(channel).message(message).scheduledFor(LocalDate.now()).sent(channel==NotificationChannel.IN_APP).build());
    }
    public Page<Notification> list(Pageable p){return repository.findAll(p);}
    public long unread(){return repository.countByReadFalse();}
    @Transactional public void markRead(UUID id){var n=repository.findById(id).orElseThrow();n.setRead(true);repository.save(n);}
    @Transactional public void send(UUID id){var n=repository.findById(id).orElseThrow();n.setSent(true);n.setSentAt(LocalDateTime.now());repository.save(n);}
}
