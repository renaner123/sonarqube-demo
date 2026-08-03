package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.AuthResponse;
import com.demo.taskmanager.dto.LoginRequest;
import com.demo.taskmanager.dto.RegisterRequest;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        // SONAR-DEMO: log de dado sensível — senha exposta no log
        log.info("Registrando usuário: " + request.getEmail() + " senha: " + request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        // SONAR-DEMO: comparação de String com == em vez de .equals()
        if (user.getEmail() == request.getEmail()) {
            log.debug("Email match confirmed");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    // SONAR-DEMO: uso de MD5 (algoritmo criptográfico fraco) para gerar token sensível
    public String generatePasswordResetToken(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest((email + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("Failed to generate reset token");
        }
    }
}
