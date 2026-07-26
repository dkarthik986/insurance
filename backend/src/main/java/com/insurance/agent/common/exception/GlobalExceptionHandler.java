package com.insurance.agent.common.exception;
import com.insurance.agent.common.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<?> notFound(EntityNotFoundException e) { return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage())); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> forbidden() { return ResponseEntity.status(403).body(ApiResponse.error("You do not have permission for this action")); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage()).collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflict() { return ResponseEntity.status(409).body(ApiResponse.error("A record with this unique value already exists")); }
    @ExceptionHandler(JwtException.class)
    ResponseEntity<?> jwt() { return ResponseEntity.status(401).body(ApiResponse.error("Authentication token is invalid or expired")); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> unexpected(Exception e) { return ResponseEntity.internalServerError().body(ApiResponse.error("The request could not be completed")); }
}

