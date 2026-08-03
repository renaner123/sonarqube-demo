package com.demo.taskmanager.service;

import com.demo.taskmanager.domain.entity.User;
import com.demo.taskmanager.domain.repository.UserRepository;
import com.demo.taskmanager.dto.UserResponse;
import com.demo.taskmanager.exception.ResourceNotFoundException;
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
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void findByEmail_shouldReturnUserResponse_whenFound() {
        User user = User.builder()
                .id(1L).name("Alice").email("alice@test.com").passwordHash("hash").build();
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        UserResponse result = userService.findByEmail("alice@test.com");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("alice@test.com");
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void findByEmail_shouldThrow_whenEmailNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // SONAR-DEMO: findById e updateUser sem cobertura — intencional para demonstração
}
