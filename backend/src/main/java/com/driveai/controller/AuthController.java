package com.driveai.controller;

import com.driveai.model.User;
import com.driveai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
        
        try {
            String googleId = principal.getAttribute("id");
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");
            
            // Find or create user
            User user = userRepository.findByGoogleId(googleId)
                    .orElseGet(() -> {
                        User newUser = new User(googleId, email, name);
                        return userRepository.save(newUser);
                    });
            
            // Update user info if needed
            if (!email.equals(user.getEmail()) || !name.equals(user.getName())) {
                user.setEmail(email);
                user.setName(name);
                userRepository.save(user);
            }
            
            Map<String, Object> userInfo = Map.of(
                "authenticated", true,
                "id", user.getId(),
                "googleId", user.getGoogleId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "createdAt", user.getCreatedAt()
            );
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("authenticated", false, "error", e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(@AuthenticationPrincipal OAuth2User principal) {
        boolean authenticated = principal != null;
        Map<String, Object> status = Map.of(
            "authenticated", authenticated,
            "timestamp", LocalDateTime.now()
        );
        
        if (authenticated) {
            status = Map.of(
                "authenticated", true,
                "email", principal.getAttribute("email"),
                "name", principal.getAttribute("name"),
                "timestamp", LocalDateTime.now()
            );
        }
        
        return ResponseEntity.ok(status);
    }
}
