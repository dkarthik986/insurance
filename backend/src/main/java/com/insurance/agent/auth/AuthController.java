package com.insurance.agent.auth;
import com.insurance.agent.auth.dto.*;
import com.insurance.agent.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @PostMapping("/login") ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) { return ResponseEntity.ok(ApiResponse.ok(service.login(request))); }
    @PostMapping("/refresh") ResponseEntity<?> refresh(@RequestBody Map<String,String> body) { return ResponseEntity.ok(ApiResponse.ok(service.refresh(body.get("refreshToken")))); }
    @PostMapping("/logout") ResponseEntity<?> logout(@RequestBody Map<String,String> body) { service.logout(body.get("refreshToken")); return ResponseEntity.ok(ApiResponse.ok(null, "Logged out")); }
    @PostMapping("/customer-login") ResponseEntity<?> customerLogin(@RequestBody Map<String,String> body) {
        return ResponseEntity.ok(ApiResponse.ok(service.createCustomerLogin(UUID.fromString(body.get("customerId")), body.get("email"), body.get("password"))));
    }
}

