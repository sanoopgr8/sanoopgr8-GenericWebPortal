package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeycloakConfigRepository extends JpaRepository<KeycloakConfig, Long> {
    
    // Find the first (and should be only) configuration
    KeycloakConfig findFirstByOrderByIdAsc();
}
