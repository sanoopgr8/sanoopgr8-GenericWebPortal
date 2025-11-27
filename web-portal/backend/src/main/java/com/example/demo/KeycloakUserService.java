package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Synchronize Keycloak user with local database
     * Creates new user or updates existing user based on Keycloak ID
     */
    public User syncUser(Jwt jwt) {
        // Extract user information from JWT claims
        String keycloakId = jwt.getSubject(); // 'sub' claim contains unique user ID
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        // Try to find existing user by Keycloak ID
        User user = userRepository.findByKeycloakId(keycloakId);
        
        if (user == null) {
            // Try to find by email (in case user was created locally first)
            user = userRepository.findByEmail(email);
            
            if (user != null) {
                // Update existing local user to link with Keycloak
                user.setKeycloakId(keycloakId);
                user.setAuthProvider("KEYCLOAK");
            } else {
                // Create new user
                user = new User();
                user.setKeycloakId(keycloakId);
                user.setAuthProvider("KEYCLOAK");
                user.setEmail(email);
                user.setVerified(true); // SSO users are pre-verified
            }
        }
        
        // Update user information from Keycloak
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (email != null) {
            user.setEmail(email);
        }
        
        // Save and return user
        return userRepository.save(user);
    }
    
    /**
     * Get user from JWT token
     */
    public User getUserFromJwt(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        User user = userRepository.findByKeycloakId(keycloakId);
        
        if (user == null) {
            // Sync user if not found
            user = syncUser(jwt);
        }
        
        return user;
    }
}
