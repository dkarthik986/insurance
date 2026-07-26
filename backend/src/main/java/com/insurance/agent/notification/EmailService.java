package com.insurance.agent.notification;

import com.insurance.agent.policy.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username:}") private String sender;

    public void sendPolicyMessage(Policy policy, String subject, String message) {
        if (policy.getCustomer().getEmail() == null || policy.getCustomer().getEmail().isBlank() || sender.isBlank()) return;
        var mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(policy.getCustomer().getEmail());
        mail.setSubject(subject);
        mail.setText("Dear " + policy.getCustomer().getName() + ",\n\n" + message
            + "\n\nPolicy No: " + policy.getPolicyNumber() + "\nCompany: " + policy.getCompany()
            + "\nPlan: " + policy.getPlanName() + "\n\nRegards,\nInsureDesk");
        mailSender.send(mail);
    }

    public void sendPasswordReset(String email, String resetUrl) {
        if (sender.isBlank() || email == null || email.isBlank()) return;
        var mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(email);
        mail.setSubject("Reset your InsureDesk password");
        mail.setText("Use this secure link to reset your password (valid for 30 minutes):\n\n"
            + resetUrl + "\n\nIf you did not request this, you can ignore this message.");
        mailSender.send(mail);
    }
}
