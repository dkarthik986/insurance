package com.insurance.agent.config;
import com.insurance.agent.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;
    @Override public UserDetails loadUserByUsername(String email) {
        return users.findByEmailIgnoreCaseAndDeletedFalse(email)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}

