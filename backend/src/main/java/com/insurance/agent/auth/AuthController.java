package com.insurance.agent.auth;

import com.insurance.agent.auth.dto.*;
import com.insurance.agent.common.dto.ApiResponse;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @Value("${app.auth.refresh-cookie-name:INSUREDESK_REFRESH}") private String cookieName;
    @Value("${app.auth.cookie-secure:false}") private boolean secure;
    @Value("${app.auth.cookie-same-site:Lax}") private String sameSite;
    @Value("${app.auth.cookie-path:/api/v1/auth}") private String cookiePath;
    @Value("${app.jwt.refresh-token-expiry-ms}") private long refreshExpiryMs;

    @PostMapping("/login")
    ResponseEntity<?> login(@Valid @RequestBody AuthRequest request, HttpServletRequest http) {
        var issued = service.login(request, http.getHeader("User-Agent"), http.getRemoteAddr());
        return ResponseEntity.ok(withCookie(ApiResponse.ok(issued.response()), issued.refreshToken()));
    }

    @PostMapping("/refresh")
    ResponseEntity<?> refresh(@CookieValue(value = "${app.auth.refresh-cookie-name:INSUREDESK_REFRESH}", required = false) String cookie,
                              @RequestBody(required = false) RefreshRequest localBody, HttpServletRequest http) {
        String token = cookie != null ? cookie : (localBody == null ? null : localBody.refreshToken());
        var issued = service.refresh(token, http.getHeader("User-Agent"), http.getRemoteAddr());
        return ResponseEntity.ok(withCookie(ApiResponse.ok(issued.response()), issued.refreshToken()));
    }

    @PostMapping("/logout")
    ResponseEntity<?> logout(@CookieValue(value = "${app.auth.refresh-cookie-name:INSUREDESK_REFRESH}", required = false) String cookie) {
        service.logout(cookie);
        return ResponseEntity.ok(clearCookie(ApiResponse.ok(null, "Logged out")));
    }

    @PostMapping("/logout-all")
    ResponseEntity<?> logoutAll(@AuthenticationPrincipal User user) {
        service.logoutAll(user.getId());
        return ResponseEntity.ok(clearCookie(ApiResponse.ok(null, "All sessions revoked")));
    }

    @GetMapping("/me")
    ResponseEntity<?> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(service.me(user)));
    }

    @PostMapping("/customer-login")
    ResponseEntity<?> customerLogin(@Valid @RequestBody CreateCustomerLoginRequest request, HttpServletRequest http) {
        var issued = service.createCustomerLogin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(withCookie(ApiResponse.ok(issued.response()), issued.refreshToken()));
    }

    @PostMapping("/forgot-password")
    ResponseEntity<?> forgot(@Valid @RequestBody ForgotPasswordRequest request) {
        service.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "If the account exists, reset instructions will be sent"));
    }

    @PostMapping("/reset-password")
    ResponseEntity<?> reset(@Valid @RequestBody ResetPasswordRequest request) {
        service.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password reset completed"));
    }

    private <T> ResponseEntity.BodyBuilder cookie(ResponseEntity.BodyBuilder builder, String value, long maxAge) {
        var cookie = ResponseCookie.from(cookieName, value).httpOnly(true).secure(secure).sameSite(sameSite)
            .path(cookiePath).maxAge(java.time.Duration.ofMillis(maxAge)).build();
        return builder.header(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    private <T> ResponseEntity<ApiResponse<T>> withCookie(ApiResponse<T> body, String refresh) {
        return cookie(ResponseEntity.ok(), refresh, refreshExpiryMs).body(body);
    }
    private <T> ResponseEntity<ApiResponse<T>> clearCookie(ApiResponse<T> body) {
        return cookie(ResponseEntity.ok(), "", 0).body(body);
    }
}

