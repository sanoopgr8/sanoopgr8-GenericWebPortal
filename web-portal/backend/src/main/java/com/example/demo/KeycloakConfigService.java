package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KeycloakConfigService {
    
    @Autowired
    private KeycloakConfigRepository repository;
    
    @Value("${keycloak.enabled:true}")
    private Boolean defaultEnabled;
    
    @Value("${keycloak.server-url:http://localhost:8180}")
    private String defaultServerUrl;
    
    @Value("${keycloak.realm:webportal}")
    private String defaultRealm;
    
    @Value("${keycloak.client-id:webportal-client}")
    private String defaultClientId;
    
    @Value("${keycloak.client-secret:}")
    private String defaultClientSecret;
    
    /**
     * Get current Keycloak configuration
     * Priority: Database > Application Properties
     */
    public KeycloakConfig getConfig() {
        KeycloakConfig dbConfig = repository.findFirstByOrderByIdAsc();
        
        if (dbConfig != null) {
            return dbConfig;
        }
        
        // Return default configuration from application.properties
        KeycloakConfig defaultConfig = new KeycloakConfig();
        defaultConfig.setEnabled(defaultEnabled);
        defaultConfig.setServerUrl(defaultServerUrl);
        defaultConfig.setRealm(defaultRealm);
        defaultConfig.setClientId(defaultClientId);
        defaultConfig.setClientSecret(defaultClientSecret);
        
        return defaultConfig;
    }
    
    /**
     * Save or update Keycloak configuration
     */
    public KeycloakConfig saveConfig(KeycloakConfig config) {
        KeycloakConfig existing = repository.findFirstByOrderByIdAsc();
        
        if (existing != null) {
            // Update existing configuration
            existing.setEnabled(config.getEnabled());
            existing.setServerUrl(config.getServerUrl());
            existing.setRealm(config.getRealm());
            existing.setClientId(config.getClientId());
            existing.setClientSecret(config.getClientSecret());
            return repository.save(existing);
        } else {
            // Create new configuration
            return repository.save(config);
        }
    }
    
    /**
     * Test connection to Keycloak server
     */
    public Map<String, Object> testConnection(KeycloakConfig config) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String wellKnownUrl = config.getServerUrl() + "/realms/" + config.getRealm() + 
                                 "/.well-known/openid-configuration";
            
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(wellKnownUrl, Map.class);
            
            if (response != null && response.containsKey("issuer")) {
                result.put("success", true);
                result.put("message", "Successfully connected to Keycloak server");
                result.put("issuer", response.get("issuer"));
            } else {
                result.put("success", false);
                result.put("message", "Invalid response from Keycloak server");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to connect: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Build issuer URI from configuration
     */
    public String getIssuerUri() {
        KeycloakConfig config = getConfig();
        if (config.getEnabled() && config.getServerUrl() != null && config.getRealm() != null) {
            return config.getServerUrl() + "/realms/" + config.getRealm();
        }
        return null;
    }
    
    /**
     * Get authorization endpoint URL
     */
    public String getAuthorizationUri() {
        KeycloakConfig config = getConfig();
        if (config.getEnabled() && config.getServerUrl() != null && config.getRealm() != null) {
            return config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/auth";
        }
        return null;
    }
    
    /**
     * Get token endpoint URL
     */
    public String getTokenUri() {
        KeycloakConfig config = getConfig();
        if (config.getEnabled() && config.getServerUrl() != null && config.getRealm() != null) {
            return config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";
        }
        return null;
    }
    
    /**
     * Get logout endpoint URL
     */
    public String getLogoutUri() {
        KeycloakConfig config = getConfig();
        if (config.getEnabled() && config.getServerUrl() != null && config.getRealm() != null) {
            return config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/logout";
        }
        return null;
    }
    
    /**
     * Get user info endpoint URL
     */
    public String getUserInfoUri() {
        KeycloakConfig config = getConfig();
        if (config.getEnabled() && config.getServerUrl() != null && config.getRealm() != null) {
            return config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/userinfo";
        }
        return null;
    }
    
    /**
     * Mask client secret for display (show only last 4 characters)
     */
    public String maskClientSecret(String secret) {
        if (secret == null || secret.isEmpty()) {
            return "";
        }
        if (secret.length() <= 4) {
            return "****";
        }
        return "****" + secret.substring(secret.length() - 4);
    }
}
