package com.blog.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blog.demo.users.Role;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;

import lombok.RequiredArgsConstructor;

// Constructor-Based Dependency Injection.
@RequiredArgsConstructor // annotation that auto-generates a constructor for final fields
public class DataInitializer implements CommandLineRunner {
    // Spring sees that constructor and automatically injects the LikeService bean.

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        String adminUsename = System.getProperty("APP_ADMIN_USERNAME");
        String adminEmail = System.getProperty("APP_ADMIN_EMAIL");
        String adminPassword = System.getProperty("APP_ADMIN_PASSWORD");

        if (adminUsename == null || adminEmail == null || adminPassword == null) {
            System.err.println("Missing credentials .env");
            return;
        }
        if (userRepository.findByUsername(adminUsename).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsename);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("defaul admin created");
        } else {
            System.out.println("defaul admin already exist");
        }
    }
}
