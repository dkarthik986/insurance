package com.insurance.agent.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);
    Optional<User> findByRefreshTokenAndDeletedFalse(String refreshToken);
}

