package com.murat.orion.auth_service.AuthDomain.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpRequest {

    /**
     * Kullanıcı telefon numarası
     */
    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Geçerli bir telefon numarası giriniz")
    private String phoneNumber;

    /**
     * SMS ile gelen doğrulama kodu (OTP)
     */
    @NotBlank(message = "Doğrulama kodu boş olamaz")
    @Size(min = 6, max = 6, message = "Doğrulama kodu 6 haneli olmalıdır")
    private String verificationCode;
}
