package com.insurance.agent.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.*;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByRefreshTokenHashAndRevokedAtIsNull(String hash);
    List<UserSession> findByUserIdAndRevokedAtIsNull(UUID userId);
    long deleteByExpiresAtBefore(Instant now);
}

