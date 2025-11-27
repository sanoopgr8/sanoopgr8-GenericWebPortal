package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private KeycloakConfigService keycloakConfigService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())  // Disable CSRF for API
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Permit all for now, will secure later
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            String issuerUri = keycloakConfigService.getIssuerUri();
            
            if (issuerUri != null) {
                return NimbusJwtDecoder.withJwkSetUri(issuerUri + "/protocol/openid-connect/certs").build();
            }
        } catch (Exception e) {
            // Keycloak not configured, return dummy decoder
        }
        
        // Return dummy decoder if Keycloak is disabled
        return token -> {
            throw new RuntimeException("Keycloak is not enabled");
        };
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow localhost and remote access for development
        // For production, restrict to specific domains only
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "https://localhost:*",
            "http://192.168.*:*",  // Local network
            "http://10.*:*",       // Private network range
            "http://*:5173",       // Vite default port
            "http://*:5174",       // Vite alternative port
            "https://*"            // Any HTTPS (for production with proper domain)
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
