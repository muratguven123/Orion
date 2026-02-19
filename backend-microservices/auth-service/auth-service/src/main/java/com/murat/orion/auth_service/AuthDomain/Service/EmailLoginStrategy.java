package com.murat.orion.auth_service.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.EmailLoginRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.LoginResponse;
import com.murat.orion.auth_service.AuthDomain.Loginİnterface;
import com.murat.orion.auth_service.AuthDomain.Repository.UserRepository;
import com.murat.orion.auth_service.AuthDomain.Config.JwtService;
import com.murat.orion.auth_service.AuthDomain.Events.EmailLoginEvent;
import com.murat.orion.auth_service.AuthDomain.Events.LoginFailedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EmailLoginStrategy implements Loginİnterface<EmailLoginRequest> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public LoginResponse login(EmailLoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    LoginFailedEvent failedEvent = LoginFailedEvent.builder()
                            .email(loginRequest.getEmail())
                            .reason("Kullanıcı bulunamadı")
                            .failedAt(LocalDateTime.now())
                            .build();
                    applicationEventPublisher.publishEvent(failedEvent);
                    return new RuntimeException("Kullanıcı bulunamadı");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            LoginFailedEvent failedEvent = LoginFailedEvent.builder()
                    .email(loginRequest.getEmail())
                    .reason("Şifre yanlış")
                    .failedAt(LocalDateTime.now())
                    .build();
            applicationEventPublisher.publishEvent(failedEvent);
            throw new RuntimeException("Şifre yanlış");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        EmailLoginEvent event = EmailLoginEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .loginAt(LocalDateTime.now())
                .build();
        applicationEventPublisher.publishEvent(event);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .loginTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
    }

    @Override
    public String getLoginType() {
        return "EMAIL";
    }
}
