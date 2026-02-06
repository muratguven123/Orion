package org.murat.orion.AuthDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Response.RegisterResponse;
import org.murat.orion.AuthDomain.Entity.Role;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Mapper.UserMapper;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .password("password123")
                .phoneNumber("+905551234567")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .password("encodedPassword123")
                .phoneNumber("+905551234567")
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        registerResponse = RegisterResponse.builder()
                .userId(1L)
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .phoneNumber("+905551234567")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .status("SUCCESS")
                .message("Kayıt başarıyla tamamlandı")
                .build();
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully when email is not taken")
        void register_WhenEmailNotTaken_ShouldReturnRegisterResponse() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
            when(userMapper.toEntity(any(RegisterRequest.class), anyString())).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toRegisterResponse(any(User.class))).thenReturn(registerResponse);

            // Act
            RegisterResponse result = authService.register(registerRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("murat@test.com");
            assertThat(result.getFirstName()).isEqualTo("Murat");
            assertThat(result.getLastName()).isEqualTo("Test");
            assertThat(result.getRole()).isEqualTo("USER");
            assertThat(result.getStatus()).isEqualTo("SUCCESS");

            verify(userRepository).findByEmail("murat@test.com");
            verify(passwordEncoder).encode("password123");
            verify(userMapper).toEntity(registerRequest, "encodedPassword123");
            verify(userRepository).save(user);
            verify(userMapper).toRegisterResponse(user);
        }

        @Test
        @DisplayName("Should throw exception when email is already taken")
        void register_WhenEmailAlreadyTaken_ShouldThrowException() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            // Act & Assert
            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Bu email adresi zaten kayıtlı");

            verify(userRepository).findByEmail("murat@test.com");
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should encode password before saving")
        void register_ShouldEncodePassword() {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
            when(userMapper.toEntity(any(RegisterRequest.class), eq("encodedPassword123"))).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toRegisterResponse(any(User.class))).thenReturn(registerResponse);

            // Act
            authService.register(registerRequest);

            // Assert
            verify(passwordEncoder).encode("password123");
            verify(userMapper).toEntity(registerRequest, "encodedPassword123");
        }
    }
}

