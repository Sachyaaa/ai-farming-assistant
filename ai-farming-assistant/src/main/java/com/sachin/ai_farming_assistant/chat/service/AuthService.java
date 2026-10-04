package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.RegisterRequest;
import com.sachin.ai_farming_assistant.chat.entity.User;
import com.sachin.ai_farming_assistant.chat.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = new User(
                email,
                passwordHash,
                LocalDateTime.now()
        );

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}