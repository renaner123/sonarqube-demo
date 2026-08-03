package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.AuthResponse;
import com.demo.taskmanager.dto.LoginRequest;
import com.demo.taskmanager.exception.BusinessException;
import com.demo.taskmanager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsValid() {
        User user = User.builder()
                .id(1L).name("Alice").email("alice@test.com").passwordHash("encoded_pass").build();
        LoginRequest request = new LoginRequest("alice@test.com", "senha123");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "encoded_pass")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("alice@test.com");
        assertThat(response.getName()).isEqualTo("Alice");
    }

    @Test
    void login_shouldThrow_whenEmailNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown@test.com", "pass")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        User user = User.builder()
                .id(1L).name("Alice").email("alice@test.com").passwordHash("encoded_pass").build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice@test.com", "wrong_pass")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid credentials");
    }

    // SONAR-DEMO: register e validateToken sem cobertura — intencional para demonstração
}
