package com.murat.orion.auth_service.AuthDomain.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.EmailLoginRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.LoginResponse;
import com.murat.orion.auth_service.AuthDomain.Entity.OutboxEvent;
import com.murat.orion.auth_service.AuthDomain.Events.EmailLoginEvent;
import com.murat.orion.auth_service.AuthDomain.Loginİnterface;
import com.murat.orion.auth_service.AuthDomain.Repository.OutboxEventRepository;
import com.murat.orion.auth_service.AuthDomain.Repository.UserRepository;
import com.murat.orion.auth_service.AuthDomain.Config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailLoginStrategy implements Loginİnterface<EmailLoginRequest> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public LoginResponse login(EmailLoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        LocalDateTime now = LocalDateTime.now();

        EmailLoginEvent event = EmailLoginEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .subject("Email Login")
                .loginAt(now)
                .build();

        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("User");
            outboxEvent.setAggregateId(user.getId());
            outboxEvent.setEventType("EmailLoginEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Event JSON serialize hatası", e);
        }

        log.info("Email login outbox event saved for userId: {}", user.getId());

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
                .loginTime(now)
                .status("SUCCESS")
                .build();
    }

    @Override
    public String getLoginType() {
        return "EMAIL";
    }
}
