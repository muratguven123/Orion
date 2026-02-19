package com.murat.orion.auth_service.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import com.murat.orion.auth_service.AuthDomain.Config.JwtService;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.RefreshTokenRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.RegisterRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.RefreshTokenResponse;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.RegisterResponse;
import com.murat.orion.auth_service.AuthDomain.Entity.User;
import com.murat.orion.auth_service.AuthDomain.Mapper.UserMapper;
import com.murat.orion.auth_service.AuthDomain.Repository.UserRepository;
import com.murat.orion.auth_service.Notification.Events.Auth.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kayıtlı");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userMapper.toEntity(request, encodedPassword);

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .registeredAt(LocalDateTime.now())
                .build();
        applicationEventPublisher.publishEvent(event);

        return userMapper.toRegisterResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Geçersiz veya süresi dolmuş refresh token");
        }

        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        LocalDateTime now = LocalDateTime.now();
        long accessTokenExpiration = jwtService.getExpirationTime();
        long refreshTokenExpiration = jwtService.getRefreshExpirationTime();

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExpiration)
                .refreshTokenExpiresIn(refreshTokenExpiration)
                .issuedAt(now)
                .accessTokenExpiresAt(now.plusSeconds(accessTokenExpiration / 1000))
                .refreshTokenExpiresAt(now.plusSeconds(refreshTokenExpiration / 1000))
                .status("SUCCESS")
                .message("Token başarıyla yenilendi")
                .build();
    }
}
