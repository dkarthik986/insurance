package com.insurance.agent.config;

import com.insurance.agent.auth.User;
import com.insurance.agent.auth.UserRepository;
import com.insurance.agent.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevAdminBootstrap implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Value("${app.bootstrap.enabled:false}") private boolean enabled;
    @Value("${app.bootstrap.email:}") private String email;
    @Value("${app.bootstrap.password:}") private String password;
    @Value("${app.bootstrap.name:Development Dealer}") private String name;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) return;
        if (email.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Dev bootstrap requires BOOTSTRAP_ADMIN_EMAIL and a password of at least 12 characters");
        }
        User user = users.findByEmailIgnoreCaseAndDeletedFalse(email)
            .orElseGet(() -> User.builder().email(email).role(UserRole.DEALER).build());
        user.setName(name);
        user.setRole(UserRole.DEALER);
        user.setPasswordHash(encoder.encode(password));
        user.setActive(true);
        user.setDeleted(false);
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        users.save(user);
    }
}
