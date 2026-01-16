package org.murat.orion.AuthDomain.Mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Response.RegisterResponse;
import org.murat.orion.AuthDomain.Entity.Role;
import org.murat.orion.AuthDomain.Entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper Unit Tests")
class UserMapperTest {

    private UserMapper userMapper;

    private RegisterRequest registerRequest;
    private User user;

    private static final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

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
                .password(ENCODED_PASSWORD)
                .phoneNumber("+905551234567")
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("To Entity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should convert RegisterRequest to User entity correctly")
        void toEntity_ShouldConvertCorrectly() {
            User result = userMapper.toEntity(registerRequest, ENCODED_PASSWORD);

            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Murat");
            assertThat(result.getLastName()).isEqualTo("Test");
            assertThat(result.getEmail()).isEqualTo("murat@test.com");
            assertThat(result.getPassword()).isEqualTo(ENCODED_PASSWORD);
            assertThat(result.getPhoneNumber()).isEqualTo("+905551234567");
        }

        @Test
        @DisplayName("Should set default role as USER")
        void toEntity_ShouldSetDefaultRoleAsUser() {
            User result = userMapper.toEntity(registerRequest, ENCODED_PASSWORD);

            assertThat(result.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("Should set isActive to true")
        void toEntity_ShouldSetIsActiveToTrue() {
            User result = userMapper.toEntity(registerRequest, ENCODED_PASSWORD);

            assertThat(result.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("Should set isEmailVerified to false")
        void toEntity_ShouldSetIsEmailVerifiedToFalse() {
            User result = userMapper.toEntity(registerRequest, ENCODED_PASSWORD);

            assertThat(result.getIsEmailVerified()).isFalse();
        }

        @Test
        @DisplayName("Should use encoded password not raw password")
        void toEntity_ShouldUseEncodedPassword() {
            User result = userMapper.toEntity(registerRequest, ENCODED_PASSWORD);

            assertThat(result.getPassword()).isEqualTo(ENCODED_PASSWORD);
            assertThat(result.getPassword()).isNotEqualTo("password123");
        }

        @Test
        @DisplayName("Should handle null phone number")
        void toEntity_WithNullPhoneNumber_ShouldConvert() {
            RegisterRequest requestWithoutPhone = RegisterRequest.builder()
                    .firstName("Murat")
                    .lastName("Test")
                    .email("murat@test.com")
                    .password("password123")
                    .phoneNumber(null)
                    .build();

            User result = userMapper.toEntity(requestWithoutPhone, ENCODED_PASSWORD);

            assertThat(result).isNotNull();
            assertThat(result.getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("To RegisterResponse Tests")
    class ToRegisterResponseTests {

        @Test
        @DisplayName("Should convert User entity to RegisterResponse correctly")
        void toRegisterResponse_ShouldConvertCorrectly() {
            RegisterResponse result = userMapper.toRegisterResponse(user);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("Murat");
            assertThat(result.getLastName()).isEqualTo("Test");
            assertThat(result.getEmail()).isEqualTo("murat@test.com");
            assertThat(result.getPhoneNumber()).isEqualTo("+905551234567");
        }

        @Test
        @DisplayName("Should set status as SUCCESS")
        void toRegisterResponse_ShouldSetStatusAsSuccess() {
            RegisterResponse result = userMapper.toRegisterResponse(user);

            assertThat(result.getStatus()).isEqualTo("SUCCESS");
        }

        @Test
        @DisplayName("Should set success message")
        void toRegisterResponse_ShouldSetSuccessMessage() {
            RegisterResponse result = userMapper.toRegisterResponse(user);

            assertThat(result.getMessage()).isEqualTo("Kayıt başarıyla tamamlandı");
        }

        @Test
        @DisplayName("Should include role in response")
        void toRegisterResponse_ShouldIncludeRole() {
            RegisterResponse result = userMapper.toRegisterResponse(user);

            assertThat(result.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Should include createdAt in response")
        void toRegisterResponse_ShouldIncludeCreatedAt() {
            RegisterResponse result = userMapper.toRegisterResponse(user);

            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should handle user with ADMIN role")
        void toRegisterResponse_WithAdminRole_ShouldConvert() {
            User adminUser = User.builder()
                    .id(2L)
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@test.com")
                    .password(ENCODED_PASSWORD)
                    .role(Role.ADMIN)
                    .isActive(true)
                    .isEmailVerified(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            RegisterResponse result = userMapper.toRegisterResponse(adminUser);

            assertThat(result.getRole()).isEqualTo("ADMIN");
        }
    }
}

