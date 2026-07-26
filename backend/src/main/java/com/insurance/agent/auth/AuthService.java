package com.insurance.agent.auth;

import com.insurance.agent.auth.dto.*;
import com.insurance.agent.common.enums.UserRole;
import com.insurance.agent.config.JwtService;
import com.insurance.agent.customer.CustomerRepository;
import com.insurance.agent.notification.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final UserSessionRepository sessions;
    private final PasswordResetTokenRepository resetTokens;
    private final CustomerRepository customers;
    private final JwtService jwt;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    @Value("${app.jwt.refresh-token-expiry-ms}") private long refreshExpiryMs;
    @Value("${app.auth.max-login-attempts:5}") private int maxLoginAttempts;
    @Value("${app.auth.lock-minutes:15}") private long lockMinutes;
    @Value("${app.frontend.reset-url:http://localhost:3000/reset-password}") private String resetUrl;

    @Transactional
    public IssuedAuth login(AuthRequest request, String userAgent, String ipAddress) {
        User user = users.findByEmailIgnoreCaseAndDeletedFalse(request.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() >= maxLoginAttempts) {
                user.setLockedUntil(Instant.now().plus(lockMinutes, java.time.temporal.ChronoUnit.MINUTES));
                user.setFailedLoginCount(0);
            }
            users.save(user);
            throw new BadCredentialsException("Invalid email or password");
        }
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        users.save(user);
        return issue(user, userAgent, ipAddress);
    }

    @Transactional
    public IssuedAuth refresh(String rawToken, String userAgent, String ipAddress) {
        if (rawToken == null || rawToken.isBlank()) throw new BadCredentialsException("Invalid refresh session");
        UserSession current = sessions.findByRefreshTokenHashAndRevokedAtIsNull(hash(rawToken))
            .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
            .orElseThrow(() -> new BadCredentialsException("Invalid refresh session"));
        current.setRevokedAt(Instant.now());
        current.setLastUsedAt(Instant.now());
        sessions.save(current);
        return issue(current.getUser(), userAgent, ipAddress);
    }

    @Transactional public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        sessions.findByRefreshTokenHashAndRevokedAtIsNull(hash(rawToken)).ifPresent(s -> { s.setRevokedAt(Instant.now()); sessions.save(s); });
    }

    @Transactional public void logoutAll(UUID userId) {
        var active = sessions.findByUserIdAndRevokedAtIsNull(userId);
        active.forEach(s -> s.setRevokedAt(Instant.now()));
        sessions.saveAll(active);
    }

    @Transactional
    public IssuedAuth createCustomerLogin(CreateCustomerLoginRequest request) {
        var customer = customers.findByIdAndDeletedFalse(request.customerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        var user = users.save(User.builder().name(customer.getName()).email(request.email()).phone(customer.getPhone())
            .passwordHash(encoder.encode(request.temporaryPassword())).role(UserRole.CUSTOMER).build());
        customer.setUser(user);
        customer.setEmail(request.email());
        customers.save(customer);
        return issue(user, null, null);
    }

    public Map<String, Object> me(User user) {
        return Map.of("id", user.getId(), "name", user.getName(), "email", user.getEmail(), "phone", Objects.toString(user.getPhone(), ""), "role", user.getRole());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        users.findByEmailIgnoreCaseAndDeletedFalse(request.email()).ifPresent(user -> {
            String raw = UUID.randomUUID().toString() + UUID.randomUUID();
            resetTokens.save(PasswordResetToken.builder().user(user).tokenHash(hash(raw))
                .expiresAt(Instant.now().plus(30, java.time.temporal.ChronoUnit.MINUTES)).build());
            emailService.sendPasswordReset(user.getEmail(), resetUrl + "?token=" + raw);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var token = resetTokens.findByTokenHashAndUsedAtIsNull(hash(request.token()))
            .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
            .orElseThrow(() -> new BadCredentialsException("Invalid or expired reset token"));
        token.getUser().setPasswordHash(encoder.encode(request.newPassword()));
        users.save(token.getUser());
        token.setUsedAt(Instant.now());
        resetTokens.save(token);
        logoutAll(token.getUser().getId());
    }

    private IssuedAuth issue(User user, String userAgent, String ipAddress) {
        String rawRefresh = UUID.randomUUID().toString() + UUID.randomUUID();
        sessions.save(UserSession.builder().user(user).refreshTokenHash(hash(rawRefresh))
            .expiresAt(Instant.now().plusMillis(refreshExpiryMs)).userAgent(userAgent).ipAddress(ipAddress).build());
        return new IssuedAuth(new AuthResponse(jwt.generateAccessToken(user), user.getRole(), user.getId(), user.getName(), user.getEmail()), rawRefresh);
    }

    public static String hash(String raw) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
