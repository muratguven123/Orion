package org.murat.orion.AuthDomain.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murat.orion.AuthDomain.Dto.Request.EmailLoginRequest;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Request.SendOtpRequest;
import org.murat.orion.AuthDomain.Dto.Request.VerifyOtpRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Dto.Response.OtpResponse;
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
    public ResponseEntity<LoginResponse> loginWithEmail(@Valid @RequestBody EmailLoginRequest request) {
        LoginResponse response = emailLoginStrategy.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * SMS Login - Adım 1: OTP Gönder
     * Telefon numarasına doğrulama kodu gönderir
     */
    @PostMapping("/login/sms/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        OtpResponse response = smsLoginStrategy.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * SMS Login - Adım 2: OTP Doğrula ve Login
     * Telefon numarası ve doğrulama kodu ile sisteme giriş yapar
     */
    @PostMapping("/login/sms/verify")
    public ResponseEntity<LoginResponse> verifyOtpAndLogin(@Valid @RequestBody VerifyOtpRequest request) {
        LoginResponse response = smsLoginStrategy.verifyOtpAndLogin(request);
        return ResponseEntity.ok(response);
    }
}

