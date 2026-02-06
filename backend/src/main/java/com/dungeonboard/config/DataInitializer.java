package com.dungeonboard.config;

import com.dungeonboard.model.User;
import com.dungeonboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Only create test user if it doesn't exist
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = new User();
                testUser.setUsername("testuser");
                testUser.setPassword(passwordEncoder.encode("password123"));
                userRepository.save(testUser);
                System.out.println("✓ Test user created: testuser / password123");
            } else {
                System.out.println("✓ Test user already exists: testuser");
            }
        };
    }
}
