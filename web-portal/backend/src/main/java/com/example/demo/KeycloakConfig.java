package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "keycloak_configs")
public class KeycloakConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "server_url", length = 500)
    private String serverUrl;
    
    @Column(length = 100)
    private String realm;
    
    @Column(name = "client_id", length = 100)
    private String clientId;
    
    @Column(name = "client_secret", length = 500)
    private String clientSecret;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getServerUrl() {
        return serverUrl;
    }
    
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public String getRealm() {
        return realm;
    }
    
    public void setRealm(String realm) {
        this.realm = realm;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
