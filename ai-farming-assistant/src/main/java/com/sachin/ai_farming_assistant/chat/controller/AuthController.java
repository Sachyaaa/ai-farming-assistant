package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.LoginRequest;
import com.sachin.ai_farming_assistant.chat.dto.RegisterRequest;
import com.sachin.ai_farming_assistant.chat.entity.User;
import com.sachin.ai_farming_assistant.chat.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String USER_ID = "USER_ID";

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository
    ) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestBody RegisterRequest request
    ) {

        User user = authService.register(request);

        return Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(
                        authenticationRequest
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        User user = authService.findByEmail(request.email());

        return Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout(
            HttpSession session
    ) {

        session.invalidate();

        return Map.of(
                "message",
                "Logged out successfully"
        );
    }
}