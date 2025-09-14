package com.lunar.demo.controller;

import com.lunar.demo.dto.LoginRequest;
import com.lunar.demo.dto.LoginResponse;
import com.lunar.demo.dto.RegisterRequest;
import com.lunar.demo.dto.UserResponse;
import com.lunar.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username/email: {}", request.getUsernameOrEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        log.info("Token refresh attempt");
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout attempt");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        log.info("Get current user request");
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserResponse userResponse) {
        log.info("Profile update attempt for user: {}", userResponse.getUsername());
        UserResponse updatedUser = authService.updateProfile(userResponse);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestParam String currentPassword, 
                                            @RequestParam String newPassword) {
        log.info("Password change attempt");
        authService.changePassword(currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}
