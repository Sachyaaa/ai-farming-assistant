package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.entity.User;
import com.sachin.ai_farming_assistant.chat.repository.UserRepository;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getAuthenticatedUserId(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new IllegalStateException(
                    "User must be authenticated"
            );
        }

        String username = authentication.getName();

        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Authenticated user not found"
                        )
                );

        return user.getId();
    }
}