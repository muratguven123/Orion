package com.murat.orion.auth_service.AuthDomain.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.EmailLoginRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.RefreshTokenRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.RegisterRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.SendOtpRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.VerifyOtpRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.LoginResponse;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.OtpResponse;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.RefreshTokenResponse;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.RegisterResponse;
import com.murat.orion.auth_service.AuthDomain.Service.AuthService;
import com.murat.orion.auth_service.AuthDomain.Service.EmailLoginStrategy;
import com.murat.orion.auth_service.AuthDomain.Service.SmsLoginStrategy;
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


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login/email")
    public ResponseEntity<LoginResponse> loginWithEmail(@Valid @RequestBody EmailLoginRequest request) {
        LoginResponse response = emailLoginStrategy.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login/sms/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        OtpResponse response = smsLoginStrategy.sendOtp(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login/sms/verify")
    public ResponseEntity<LoginResponse> verifyOtpAndLogin(@Valid @RequestBody VerifyOtpRequest request) {
        LoginResponse response = smsLoginStrategy.verifyOtpAndLogin(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
