package org.murat.orion.AuthDomain.Dto.Response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    /**
     * Oluşturulan kullanıcının ID'si
     */
    private Long userId;

    /**
     * Kayıtlı email adresi
     */
    private String email;

    /**
     * Kayıtlı telefon numarası (varsa)
     */
    private String phoneNumber;

    /**
     * Kullanıcı adı
     */
    private String firstName;

    /**
     * Kullanıcı soyadı
     */
    private String lastName;

    /**
     * Kullanıcı rolü
     */
    private String role;

    /**
     * Hesap durumu (örn: "PENDING_VERIFICATION", "ACTIVE")
     */
    private String status;

    /**
     * Kayıt tarihi
     */
    private LocalDateTime createdAt;

    /**
     * Başarı mesajı
     */
    private String message;

    /**
     * Doğrulama gerekli mi? (Email/SMS doğrulaması için)
     */
    private Boolean requiresVerification;

    /**
     * Doğrulama kodu gönderildi mi?
     */
    private Boolean verificationCodeSent;
}
