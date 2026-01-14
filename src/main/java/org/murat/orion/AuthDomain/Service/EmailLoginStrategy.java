package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.AuthDomain.Dto.Request.EmailLoginRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Loginİnterface;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.murat.orion.AuthDomain.Config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EmailLoginStrategy implements Loginİnterface<EmailLoginRequest> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(EmailLoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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
