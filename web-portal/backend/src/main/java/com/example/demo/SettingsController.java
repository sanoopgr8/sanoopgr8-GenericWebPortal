package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SettingsController {

    @Autowired
    private MailConfigRepository mailConfigRepository;
    
    @Autowired
    private KeycloakConfigService keycloakConfigService;

    @GetMapping("/mail")
    public ResponseEntity<MailConfig> getMailConfig() {
        // We assume there is always one row with ID 1
        return mailConfigRepository.findById(1L)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/mail")
    public ResponseEntity<MailConfig> updateMailConfig(@RequestBody MailConfig newConfig) {
        newConfig.setId(1L); // Ensure we always update the single record
        MailConfig saved = mailConfigRepository.save(newConfig);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/keycloak")
    public ResponseEntity<Map<String, Object>> getKeycloakConfig() {
        KeycloakConfig config = keycloakConfigService.getConfig();
        
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", config.getEnabled());
        response.put("serverUrl", config.getServerUrl());
        response.put("realm", config.getRealm());
        response.put("clientId", config.getClientId());
        
        // Mask client secret for security
        String maskedSecret = keycloakConfigService.maskClientSecret(config.getClientSecret());
        response.put("clientSecret", maskedSecret);
        response.put("clientSecretMasked", true);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/keycloak")
    public ResponseEntity<Map<String, Object>> updateKeycloakConfig(@RequestBody KeycloakConfig newConfig) {
        try {
            KeycloakConfig saved = keycloakConfigService.saveConfig(newConfig);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Keycloak configuration updated successfully");
            response.put("config", saved);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update configuration: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/keycloak/test")
    public ResponseEntity<Map<String, Object>> testKeycloakConnection(@RequestBody KeycloakConfig config) {
        Map<String, Object> result = keycloakConfigService.testConnection(config);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
