package org.murat.orion.AuthDomain.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murat.orion.AuthDomain.Dto.Request.LoginRequest;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Dto.Response.RegisterResponse;
import org.murat.orion.AuthDomain.Service.AuthService;
import org.murat.orion.AuthDomain.Service.EmailLoginStrategy;
import org.murat.orion.AuthDomain.Service.SmsLoginStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailLoginStrategy emailLoginStrategy;
    private final SmsLoginStrategy smsLoginStrategy;

    /**
     * Yeni kullanıcı kaydı
     * Rol otomatik olarak USER atanır
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Email ile login
     */
    @PostMapping("/login/email")
    public ResponseEntity<LoginResponse> loginWithEmail(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = emailLoginStrategy.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * SMS ile login
     * İlk çağrıda OTP gönderilir, ikinci çağrıda verificationCode ile doğrulama yapılır
     */
    @PostMapping("/login/sms")
    public ResponseEntity<LoginResponse> loginWithSms(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = smsLoginStrategy.login(request);
        return ResponseEntity.ok(response);
    }
}

