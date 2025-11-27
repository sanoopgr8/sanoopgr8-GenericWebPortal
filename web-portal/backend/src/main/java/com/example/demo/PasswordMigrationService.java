package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * One-time migration service to hash existing plain text passwords.
 * This will run automatically on startup and update any unhashed passwords.
 */
@Component
public class PasswordMigrationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String password = user.getPassword();

            // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
            if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                System.out.println("Migrating password for user: " + user.getEmail());
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("Password migrated successfully for: " + user.getEmail());
            }
        }

        System.out.println("Password migration completed!");
    }
}
