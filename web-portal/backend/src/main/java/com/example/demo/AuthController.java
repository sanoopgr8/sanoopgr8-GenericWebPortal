package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$");

    @PostMapping("/signup")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Map<String, String>> signup(
            @RequestBody Map<String, String> signupData,
            jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate input
            String firstName = signupData.get("firstName");
            String lastName = signupData.get("lastName");
            String email = signupData.get("email");
            String password = signupData.get("password");
            String confirmPassword = signupData.get("confirmPassword");

            // Validation checks
            if (firstName == null || firstName.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "First name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (lastName == null || lastName.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Last name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
                response.put("status", "error");
                response.put("message", "Valid email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
                response.put("status", "error");
                response.put("message", "Password must be at least 8 characters with uppercase, lowercase, number and special character");
                return ResponseEntity.badRequest().body(response);
            }

            if (!password.equals(confirmPassword)) {
                response.put("status", "error");
                response.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if email already exists
            User existingUser = userRepository.findByEmail(email);
            User user;
            
            if (existingUser != null) {
                if (existingUser.isVerified()) {
                    response.put("status", "error");
                    response.put("message", "Email already registered");
                    return ResponseEntity.badRequest().body(response);
                }
                // If user exists but not verified, update the existing record
                user = existingUser;
            } else {
                // Create new user
                user = new User();
                user.setEmail(email.toLowerCase().trim());
            }

            // Update/Set user details
            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());
            user.setPassword(passwordEncoder.encode(password)); // Hash password with BCrypt
            user.setVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setTokenCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            // Send verification email with request context
            emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), user.getVerificationToken(), request);

            response.put("status", "success");
            response.put("message", "Registration successful! Please check your email to verify your account.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        
        User user = userRepository.findByVerificationToken(token);
        
        if (user == null) {
            response.put("status", "error");
            response.put("message", "Invalid verification token");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if token is expired (24 hours)
        if (user.getTokenCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            response.put("status", "error");
            response.put("message", "Verification token has expired");
            return ResponseEntity.badRequest().body(response);
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Email verified successfully! You can now log in.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        Map<String, String> response = new HashMap<>();

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            System.out.println("=== Login Request ===");
            System.out.println("Login attempt for email: " + email);
            System.out.println("Password length: " + (password != null ? password.length() : "null"));
            System.out.println("Password first char: " + (password != null && password.length() > 0 ? (int)password.charAt(0) : "N/A"));

            User user = userRepository.findByEmail(email);
            
            if (user == null) {
                System.out.println("User not found: " + email);
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("User found: " + user.getEmail() + ", verified: " + user.isVerified());

            // Temporarily disabled for debugging
            /*
            if (!user.isVerified()) {
                response.put("status", "error");
                response.put("message", "Please verify your email before logging in");
                return ResponseEntity.badRequest().body(response);
            }
            */

            // Verify password using BCrypt
            if (!passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Password mismatch for user: " + email);
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @Autowired
    private KeycloakConfigService keycloakConfigService;
    
    @Autowired
    private KeycloakUserService keycloakUserService;
    
    /**
     * Get SSO configuration for frontend
     */
    @GetMapping("/auth/sso/config")
    public ResponseEntity<Map<String, Object>> getSsoConfig() {
        KeycloakConfig config = keycloakConfigService.getConfig();
        
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", config.getEnabled());
        
        if (config.getEnabled()) {
            response.put("serverUrl", config.getServerUrl());
            response.put("realm", config.getRealm());
            response.put("clientId", config.getClientId());
            response.put("authorizationUrl", keycloakConfigService.getAuthorizationUri());
            response.put("tokenUrl", keycloakConfigService.getTokenUri());
            response.put("logoutUrl", keycloakConfigService.getLogoutUri());
            response.put("userInfoUrl", keycloakConfigService.getUserInfoUri());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current user info (supports both local and SSO)
     */
    @GetMapping("/auth/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Check if this is an SSO request with JWT token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // This would be handled by Spring Security's JWT decoder
                // For now, return a placeholder response
                response.put("authType", "SSO");
                response.put("message", "SSO user info would be extracted from JWT");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("status", "error");
                response.put("message", "Invalid token");
                return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
            }
        }
        
        // For local authentication, session-based user info would go here
        response.put("authType", "LOCAL");
        response.put("message", "Local user info");
        return ResponseEntity.ok(response);
    }
}
