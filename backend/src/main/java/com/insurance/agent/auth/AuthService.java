package com.insurance.agent.auth;
import com.insurance.agent.auth.dto.*;
import com.insurance.agent.common.enums.UserRole;
import com.insurance.agent.config.JwtService;
import com.insurance.agent.customer.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository users;
    private final CustomerRepository customers;
    private final JwtService jwt;
    private final PasswordEncoder encoder;
    @Value("${app.jwt.refresh-token-expiry-ms}") private long refreshExpiry;
    @Transactional public AuthResponse login(AuthRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = users.findByEmailIgnoreCaseAndDeletedFalse(request.email()).orElseThrow();
        return issue(user);
    }
    @Transactional public AuthResponse refresh(String token) {
        User user = users.findByRefreshTokenAndDeletedFalse(token)
            .filter(u -> u.getRefreshTokenExpiry() != null && u.getRefreshTokenExpiry().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid or expired"));
        return issue(user);
    }
    @Transactional public void logout(String token) {
        users.findByRefreshTokenAndDeletedFalse(token).ifPresent(u -> { u.setRefreshToken(null); u.setRefreshTokenExpiry(null); users.save(u); });
    }
    @Transactional public AuthResponse createCustomerLogin(UUID customerId, String email, String password) {
        var customer = customers.findByIdAndDeletedFalse(customerId).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        var user = users.save(User.builder().name(customer.getName()).email(email).phone(customer.getPhone())
            .passwordHash(encoder.encode(password)).role(UserRole.CUSTOMER).build());
        customer.setUser(user); customer.setEmail(email); customers.save(customer);
        return issue(user);
    }
    private AuthResponse issue(User user) {
        String refresh = jwt.generateRefreshToken();
        user.setRefreshToken(refresh);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusNanos(refreshExpiry * 1_000_000));
        users.save(user);
        return new AuthResponse(jwt.generateAccessToken(user), refresh, user.getRole(), user.getId(), user.getName(), user.getEmail());
    }
}

