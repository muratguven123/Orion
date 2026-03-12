package com.murat.orion.auth_service.AuthDomain.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsLoginRequest {

    /**
     * Kullanıcı telefon numarası
     */
    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Geçerli bir telefon numarası giriniz")
    private String phoneNumber;

    /**
     * SMS ile gelen doğrulama kodu (OTP)
     * İlk istekte boş olabilir - OTP gönderilir
     * İkinci istekte dolu olmalı - Doğrulama yapılır
     */
    private String verificationCode;
}

